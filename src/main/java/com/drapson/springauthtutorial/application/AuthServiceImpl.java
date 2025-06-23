package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.*;
import com.drapson.springauthtutorial.application.exceptions.*;
import com.drapson.springauthtutorial.application.out.RefreshTokenRepository;
import com.drapson.springauthtutorial.application.out.TokenProvider;
import com.drapson.springauthtutorial.application.out.UserProviderRepository;
import com.drapson.springauthtutorial.domain.User;
import com.drapson.springauthtutorial.domain.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserProviderRepository userProviderRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Value("${spring.tokens.other.refresh_expiration}")
    private long refTokenExpirationTime;

    public AuthServiceImpl(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            UserProviderRepository userProviderRepository,
            BCryptPasswordEncoder passwordEncoder,
            TokenProvider tokenProvider
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userProviderRepository = userProviderRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public User registerUser(RegisterUserDto registerUserDto) {
        if (userRepository.getUserByEmail(registerUserDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("This email address is already taken");
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

