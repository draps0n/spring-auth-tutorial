package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.*;
import com.drapson.springauthtutorial.application.exceptions.InvalidCredentialsException;
import com.drapson.springauthtutorial.application.exceptions.LinkedUserNotFoundException;
import com.drapson.springauthtutorial.application.exceptions.UserAlreadyLinkedToProviderException;
import com.drapson.springauthtutorial.application.out.OAuth2CodeService;
import com.drapson.springauthtutorial.application.out.UserProviderRepository;
import com.drapson.springauthtutorial.domain.User;
import com.drapson.springauthtutorial.domain.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class OAuth2ServiceImpl implements OAuth2Service {

    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;
    private final OAuth2CodeService oAuth2CodeService;
    private final AuthService authService;

    public OAuth2ServiceImpl(
            UserRepository userRepository,
            UserProviderRepository userProviderRepository,
            OAuth2CodeService oAuth2CodeService,
            AuthService authService
    ) {
        this.userRepository = userRepository;
        this.userProviderRepository = userProviderRepository;
        this.oAuth2CodeService = oAuth2CodeService;
        this.authService = authService;
    }

    @Override
    @Transactional
    public Optional<AuthTokens> linkNewOAuthAccount(LinkOAuthAccountDto linkOAuthAccountDto) {
        if (linkOAuthAccountDto.shouldLink()) {
            User user = userRepository.getUserById(linkOAuthAccountDto.userId())
                    .orElseThrow(() -> new LinkedUserNotFoundException("Local user to link to not found"));

            if (!authService.checkPassword(linkOAuthAccountDto.password(), user.getPassword())) {
                throw new InvalidCredentialsException("Invalid password");
            }

            if (userProviderRepository.checkIfUserHasProvider(user.getId(), linkOAuthAccountDto.provider())) {
                throw new UserAlreadyLinkedToProviderException("User is already linked to this provider");
            }

            userProviderRepository.save(new UserOAuthProvider(
                    UUID.randomUUID(),
                    linkOAuthAccountDto.provider(),
                    linkOAuthAccountDto.providerId(),
                    user
            ));

            return Optional.of(authService.issueJwtTokens(user));
        }

        return Optional.empty();
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
            AuthTokens authTokens = authService.issueJwtTokens(user);
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
            return registerNewOAuth2User(googleUserDto);
        }
    }

    private GoogleLoginDTO registerNewOAuth2User(GoogleUserDto googleUserDto) {
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

        userProviderRepository.save(new UserOAuthProvider(
                UUID.randomUUID(),
                "google",
                googleUserDto.sub(),
                createdGoogleUser
        ));

        AuthTokens authTokens = authService.issueJwtTokens(createdGoogleUser);

        return new GoogleLoginDTO(
                GoogleLoginDTO.LoginType.NEW_USER,
                authTokens.accessToken(),
                authTokens.refreshToken()
        );
    }
}
