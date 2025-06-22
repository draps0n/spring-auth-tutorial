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
    public AuthTokens linkNewOAuthAccount(LinkOAuthAccountDto linkOAuthAccountDto) {

        if (linkOAuthAccountDto.shouldLinkAccounts()) {
            User user = userRepository.getUserByEmailWithPassword(linkOAuthAccountDto.email())
                    .orElseThrow(() -> new LinkedUserNotFoundException("Local user to link to not found"));

            if (userProviderRepository.checkIfUserHasProvider(user.getId(), linkOAuthAccountDto.providerName())) {
                throw new UserAlreadyLinkedToProviderException("User is already linked to this provider");
            }

            userProviderRepository.save(new UserOAuthProvider(
                    UUID.randomUUID(),
                    linkOAuthAccountDto.providerName(),
                    linkOAuthAccountDto.providerId(),
                    user
            ));

            return generateNewAuthTokens(user);
        }

        return null;
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

        int randomNumber = ThreadLocalRandom.current().nextInt(1000, 999999);
        String username = googleUserDto.firstName().toLowerCase() + googleUserDto.lastName().toLowerCase() + randomNumber;

        Optional<User> user = userRepository.getUserByEmail(googleUserDto.email());

        if (user.isPresent()) {
            AuthTokens authTokens = issueJwtTokens(user.get());
            return new GoogleLoginDTO(
                    false,
                    authTokens.accessToken(),
                    authTokens.refreshToken(),
                    googleUserDto.sub(),
                    "google",
                    user.get().getEmail()
            );
        } else {
            User newGoogleUser = new User(
                    UUID.randomUUID(),
                    googleUserDto.email(),
                    null,
                    username,
                    googleUserDto.firstName(),
                    googleUserDto.lastName(),
                    null,
                    false,
                    false
            );
            User createdGoogleUser = userRepository.save(newGoogleUser);

            AuthTokens authTokens = issueJwtTokens(createdGoogleUser);

            return new GoogleLoginDTO(
                    true,
                    authTokens.accessToken(),
                    authTokens.refreshToken(),
                    null,
                    null,
                    null //todo: to verification
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

