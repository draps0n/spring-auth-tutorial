package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.*;
import com.drapson.springauthtutorial.application.exceptions.*;
import com.drapson.springauthtutorial.domain.User;
import com.drapson.springauthtutorial.domain.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, BCryptPasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public CreatedUser registerUser(RegisterUserDto registerUserDto) {
        User user = new User(
                UUID.randomUUID(),
                registerUserDto.email(),
                passwordEncoder.encode(registerUserDto.password()),
                registerUserDto.username()
        );

        User createdUser = userRepository.save(user);
        return new CreatedUser(
                createdUser.getId(),
                createdUser.getEmail(),
                createdUser.getUsername()
        );
    }

    @Override
    public AuthTokens loginUser(LoginUserDto loginUserDto) {
        User user = userRepository
                .getUserByEmail(loginUserDto.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(loginUserDto.password(), user.getPassword())) {
            throw new InvalidPasswordException("Provided password is invalid");
        }
        AuthTokens authTokens = generateNewAuthTokens(user);
        String hashedRefreshToken = tokenProvider.hashToken(authTokens.refreshToken());
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID(),
                hashedRefreshToken,
                user,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30), // TODO: Make this configurable
                false
        );
        refreshTokenRepository.save(refreshToken);

        return authTokens;
    }

    @Override
    public void logoutUser(String token) {

    }

    @Override
    @Transactional
    public AuthTokens refreshTokens(String refreshToken) {
        String hashedRefreshToken = tokenProvider.hashToken(refreshToken);
        RefreshToken existingRefreshToken = refreshTokenRepository
                .getRefreshTokenByHashedToken(hashedRefreshToken)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found"));

        if (existingRefreshToken.revoked()) {
            throw new RefreshTokenRevokedException("Refresh token has been revoked");
        }

        if (existingRefreshToken.expiresAt().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenExpiredException("Refresh token has expired");
        }

        User user = existingRefreshToken.user();
        AuthTokens authTokens = generateNewAuthTokens(user);
        String newHashedRefreshToken = tokenProvider.hashToken(authTokens.refreshToken());
        RefreshToken newRefreshToken = new RefreshToken(
                UUID.randomUUID(),
                newHashedRefreshToken,
                user,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30), // TODO: Make this configurable
                false
        );

        refreshTokenRepository.updateRevokedStatus(existingRefreshToken.id(), true);
        refreshTokenRepository.save(newRefreshToken);

        return authTokens;
    }

    private AuthTokens generateNewAuthTokens(User user) {
        String accessToken = tokenProvider.generateToken(user.getId(), user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken();
        return new AuthTokens(accessToken, refreshToken);
    }
}

