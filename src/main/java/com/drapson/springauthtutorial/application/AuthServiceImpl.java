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
import java.util.UUID;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserProviderRepository userProviderRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TempUserDataPort tempUserDataPort;

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
            TempUserDataPort tempUserDataPort
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userProviderRepository = userProviderRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.tempUserDataPort = tempUserDataPort;
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
    public String issueTemporaryRegistrationToken(PendingOAuthRegistration pendingOAuthRegistration) {
        String tempRegistrationToken = UUID.randomUUID().toString();
        tempUserDataPort.save(tempRegistrationToken, pendingOAuthRegistration, Duration.ofSeconds(tempTokenExpirationTime));

        return tempRegistrationToken;
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

