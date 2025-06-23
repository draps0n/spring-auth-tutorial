package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.*;
import com.drapson.springauthtutorial.application.exceptions.*;
import com.drapson.springauthtutorial.domain.User;
import com.drapson.springauthtutorial.domain.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserProviderRepository userProviderRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TempUserDataPort tempUserDataPort;
    private final OAuth2CodeService oAuth2CodeService;

    @Value("${spring.tokens.other.temp_access_expiration}")
    private long tempTokenExpirationTime;
    @Value("${spring.tokens.other.refresh_expiration}")
    private long refTokenExpirationTime;

    public AuthServiceImpl(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            UserProviderRepository userProviderRepository,
            BCryptPasswordEncoder passwordEncoder,
            TokenProvider tokenProvider,
            TempUserDataPort tempUserDataPort,
            OAuth2CodeService oAuth2CodeService
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userProviderRepository = userProviderRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.tempUserDataPort = tempUserDataPort;
        this.oAuth2CodeService = oAuth2CodeService;
    }

    @Override
    @Transactional
    public User registerUser(RegisterUserDto registerUserDto) {
        if (userRepository.getUserByEmailWithoutPassword(registerUserDto.email()).isPresent()) {
            PendingLocalRegistration pendingLocalRegistration = new PendingLocalRegistration(
                    registerUserDto.email(),
                    passwordEncoder.encode(registerUserDto.password())
            );
            String linkToken = UUID.randomUUID().toString();
            tempUserDataPort.save(linkToken, pendingLocalRegistration, Duration.ofSeconds(tempTokenExpirationTime));

            throw new EmailLinkedThroughProviderException("User with this email is linked through a provider", linkToken);
        }

        if (userRepository.getUserByEmailWithPassword(registerUserDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already has a local account");
        }

        User user = new User(
                UUID.randomUUID(),
                registerUserDto.email(),
                registerUserDto.password() == null ? null : passwordEncoder.encode(registerUserDto.password()),
                registerUserDto.username(),
                registerUserDto.firstName(),
                registerUserDto.lastName(),
                registerUserDto.birthDate(),
                registerUserDto.sendBudgetReports(),
                registerUserDto.isProfilePublic()
        );

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public AuthTokens loginUser(LoginUserDto loginUserDto) {
        User user = userRepository
                .getUserByEmailWithPassword(loginUserDto.email())
                .orElseThrow(() -> new InvalidCredentialsException("Login credentials are invalid"));

        if (!passwordEncoder.matches(loginUserDto.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Login credentials are invalid");
        }

        return generateNewAuthTokens(user);
    }

    @Override
    public void logoutUser(String token) {
        if (token.isBlank()) {
            throw new RefreshTokenNotProvidedException("Refresh token is blank value");
        }
        String hashedToken = tokenProvider.hashToken(token);

        RefreshToken refreshToken = refreshTokenRepository
                .getRefreshTokenByHashedToken(hashedToken)
                .orElseThrow(() -> new RefreshTokenUnknownException("Refresh token not found"));

        if (!refreshToken.expiresAt().isBefore(LocalDateTime.now()) && !refreshToken.revoked()) {
            refreshTokenRepository.updateRevokedStatus(refreshToken.id(), true);
        }
    }

    @Override
    @Transactional
    public AuthTokens refreshAccessToken(String refreshToken) {
        String hashedRefreshToken = tokenProvider.hashToken(refreshToken);
        RefreshToken existingRefreshToken = refreshTokenRepository
                .getRefreshTokenByHashedToken(hashedRefreshToken)
                .orElseThrow(() -> new RefreshTokenUnknownException("Refresh token not found"));

        if (existingRefreshToken.revoked()) {
            throw new RefreshTokenRevokedException("Refresh token has been revoked");
        }

        if (existingRefreshToken.expiresAt().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenExpiredException("Refresh token has expired");
        }

        User user = existingRefreshToken.user();

        return new AuthTokens(
                generateAccessToken(user),
                refreshToken
        );
    }

    @Override
    @Transactional
    public AuthTokens issueJwtTokens(User user) {
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return generateNewAuthTokens(user);
    }

    @Override
    public boolean checkIfUserHasProvider(User user, String provider) {
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return userProviderRepository.checkIfUserHasProvider(user.getId(), provider);
    }

    @Override
    @Transactional
    public Optional<AuthTokens> linkNewOAuthAccount(LinkOAuthAccountDto linkOAuthAccountDto) {
        if (linkOAuthAccountDto.shouldLink()) {
            User user = userRepository.getUserById(linkOAuthAccountDto.userId())
                    .orElseThrow(() -> new LinkedUserNotFoundException("Local user to link to not found"));

            if (userProviderRepository.checkIfUserHasProvider(user.getId(), linkOAuthAccountDto.provider())) {
                throw new UserAlreadyLinkedToProviderException("User is already linked to this provider");
            }

            userProviderRepository.save(new UserOAuthProvider(
                    UUID.randomUUID(),
                    linkOAuthAccountDto.provider(),
                    linkOAuthAccountDto.providerId(),
                    user
            ));

            return Optional.of(generateNewAuthTokens(user));
        }

        return Optional.empty();
    }

    @Override
    public String issueTemporaryRegistrationToken(PendingOAuthRegistration pendingOAuthRegistration) {
        String tempRegistrationToken = UUID.randomUUID().toString();
        tempUserDataPort.save(tempRegistrationToken, pendingOAuthRegistration, Duration.ofSeconds(tempTokenExpirationTime));

        return tempRegistrationToken;
    }

    @Override
    @Transactional
    public GoogleLoginDTO handleGoogleLogin(OAuthCodeDto oAuthCodeDto) {
        Map<String, String> tokenResponse = oAuth2CodeService.exchangeCodeForTokens(oAuthCodeDto.code(), oAuthCodeDto.codeVerifier());
        GoogleUserDto googleUserDto = oAuth2CodeService.extractUserInfoFromIdToken(tokenResponse.get("id_token"));

        Optional<UserOAuthProvider> existingOAuthUser = userProviderRepository
                .getOAuthUserByProviderAndProviderId("google", googleUserDto.sub());

        if (existingOAuthUser.isPresent()) {
            // User already exists with this Google provider ID
            User user = existingOAuthUser.get().user();
            AuthTokens authTokens = issueJwtTokens(user);
            return new GoogleLoginDTO(
                    GoogleLoginDTO.LoginType.EXISTING_USER,
                    authTokens.accessToken(),
                    authTokens.refreshToken()
            );
        }

        Optional<User> user = userRepository.getUserByEmail(googleUserDto.email());

        if (user.isPresent()) {
            // User exists, but not linked to Google
            User existingUser = user.get();
            return new GoogleLoginDTO(
                    GoogleLoginDTO.LoginType.POSSIBLE_LINK,
                    "google",
                    googleUserDto.sub(),
                    existingUser.getId()
            );
        } else {
            // User does not exist, create a new OAuth user
            String username = googleUserDto.firstName().substring(0, 3).toLowerCase() + "_" +
                    googleUserDto.lastName().substring(0, 3).toLowerCase() + "_" +
                    ThreadLocalRandom.current().nextInt(1000, 9999);

            User newGoogleUser = new User(
                    UUID.randomUUID(),
                    googleUserDto.email(),
                    null,
                    username,
                    googleUserDto.firstName(),
                    googleUserDto.lastName(),
                    null, // No birthdate provided
                    false, // Default to not sending budget reports
                    false // Default to private profile
            );
            User createdGoogleUser = userRepository.save(newGoogleUser);

            AuthTokens authTokens = issueJwtTokens(createdGoogleUser);

            return new GoogleLoginDTO(
                    GoogleLoginDTO.LoginType.NEW_USER,
                    authTokens.accessToken(),
                    authTokens.refreshToken()
            );
        }

    }

    @Transactional
    protected AuthTokens generateNewAuthTokens(User user) {
        String accessToken = tokenProvider.generateToken(user.getId(), user.getEmail());
        String rawRefreshToken = tokenProvider.generateRefreshToken();
        String hashedRefreshToken = tokenProvider.hashToken(rawRefreshToken);
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID(),
                hashedRefreshToken,
                user,
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(refTokenExpirationTime),
                false
        );

        refreshTokenRepository.save(refreshToken);

        return new AuthTokens(accessToken, rawRefreshToken);
    }

    private String generateAccessToken(User user) {
        return tokenProvider.generateToken(user.getId(), user.getEmail());
    }
}

