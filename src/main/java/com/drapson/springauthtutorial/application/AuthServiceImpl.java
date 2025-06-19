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
    private final UserProviderRepository userProviderRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TempUserDataPort tempUserDataPort;

    public AuthServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, UserProviderRepository userProviderRepository, BCryptPasswordEncoder passwordEncoder, TokenProvider tokenProvider, TempUserDataPort tempUserDataPort) {
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
            throw new EmailLinkedThroughProviderException("User with this email is linked through a provider");
        }

        if (userRepository.getUserByEmail(registerUserDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists");
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
                .getUserByEmail(loginUserDto.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(loginUserDto.password(), user.getPassword())) {
            throw new InvalidPasswordException("Provided password is invalid");
        }

        return generateNewAuthTokens(user);
    }

    @Override
    public void logoutUser(String token) {
        String hashedToken = tokenProvider.hashToken(token);
        RefreshToken refreshToken = refreshTokenRepository
                .getRefreshTokenByHashedToken(hashedToken)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found"));

        if (!refreshToken.expiresAt().isBefore(LocalDateTime.now()) && !refreshToken.revoked()) {
            refreshTokenRepository.updateRevokedStatus(refreshToken.id(), true);
        }
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

        return generateNewAuthTokens(user, existingRefreshToken.id());
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
    public AuthTokens finishOAuthRegistration(FinishOAuthRegistrationDto finishOAuthRegistrationDto) {
        String token = finishOAuthRegistrationDto.token();
        PendingOAuthRegistration pendingOAuthRegistration = (PendingOAuthRegistration) tempUserDataPort.get(token);
        if (pendingOAuthRegistration == null) {
            throw new InvalidRegistrationTokenException("Pending OAuth registration not found for token: " + token);
        }
        tempUserDataPort.delete(token);

        User user = registerUser(
                new RegisterUserDto(
                        pendingOAuthRegistration.email(),
                        null,
                        finishOAuthRegistrationDto.username(),
                        pendingOAuthRegistration.firstName(),
                        pendingOAuthRegistration.lastName(),
                        finishOAuthRegistrationDto.birthDate(),
                        finishOAuthRegistrationDto.sendBudgetReports(),
                        finishOAuthRegistrationDto.isProfilePublic()
                )
        );

        userProviderRepository.save(new UserOAuthProvider(
                UUID.randomUUID(),
                pendingOAuthRegistration.provider(),
                pendingOAuthRegistration.providerId(),
                user
        ));

        return generateNewAuthTokens(user);
    }

    @Override
    @Transactional
    public AuthTokens linkNewOAuthAccount(LinkAccountsDto linkAccountsDto) {
        PendingOAuthRegistration pendingOAuthRegistration = (PendingOAuthRegistration) tempUserDataPort.get(linkAccountsDto.linkToken());
        if (pendingOAuthRegistration == null) {
            throw new InvalidRegistrationTokenException("Pending OAuth registration not found for token: " + linkAccountsDto.linkToken());
        }
        tempUserDataPort.delete(linkAccountsDto.linkToken());

        if (linkAccountsDto.shouldLinkAccounts()) {
            User user = userRepository.getUserByEmailWithPassword(pendingOAuthRegistration.email())
                    .orElseThrow(() -> new UserNotFoundException("User not found or already has a password"));

            if (userProviderRepository.checkIfUserHasProvider(user.getId(), pendingOAuthRegistration.provider())) {
                throw new UserAlreadyLinkedToProviderException("User is already linked to this provider");
            }

            userProviderRepository.save(new UserOAuthProvider(
                    UUID.randomUUID(),
                    pendingOAuthRegistration.provider(),
                    pendingOAuthRegistration.providerId(),
                    user
            ));

            return generateNewAuthTokens(user);
        }

        return null;
    }

    @Transactional
    protected AuthTokens generateNewAuthTokens(User user) {
        return generateNewAuthTokens(user, null);
    }

    @Transactional
    protected AuthTokens generateNewAuthTokens(User user, UUID toRevokeRefreshTokenId) {
        String accessToken = tokenProvider.generateToken(user.getId(), user.getEmail());
        String rawRefreshToken = tokenProvider.generateRefreshToken();
        String hashedRefreshToken = tokenProvider.hashToken(rawRefreshToken);
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID(),
                hashedRefreshToken,
                user,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30), // TODO: Make this configurable
                false
        );

        if (toRevokeRefreshTokenId != null) {
            refreshTokenRepository.updateRevokedStatus(toRevokeRefreshTokenId, true);
        }

        refreshTokenRepository.save(refreshToken);

        return new AuthTokens(accessToken, rawRefreshToken);
    }
}

