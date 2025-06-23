package com.drapson.springauthtutorial.adapters.in.api;

import com.drapson.springauthtutorial.adapters.in.api.request.*;
import com.drapson.springauthtutorial.adapters.in.api.response.AccessTokenResponse;
import com.drapson.springauthtutorial.adapters.in.api.response.OAuth2LinkResponse;
import com.drapson.springauthtutorial.adapters.in.api.util.CookieUtil;
import com.drapson.springauthtutorial.application.AuthService;
import com.drapson.springauthtutorial.application.dtos.*;
import com.drapson.springauthtutorial.domain.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@SecurityRequirements
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    public AuthController(AuthService authService, CookieUtil cookieUtil) {
        this.authService = authService;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid RegisterUserRequest request,
                                             HttpServletResponse response) {
        User user = authService.registerUser(
                new RegisterUserDto(
                        request.email(),
                        request.password(),
                        request.username(),
                        request.firstName(),
                        request.lastName(),
                        request.birthDate(),
                        request.sendBudgetReports(),
                        request.isProfilePublic()
                )
        );
        AuthTokens authTokens = authService.issueJwtTokens(user);

        Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authTokens.refreshToken());
        Cookie accessTokenCookie = cookieUtil.createAccessTokenCookie(authTokens.accessToken());

        response.addCookie(refreshTokenCookie);
        response.addCookie(accessTokenCookie);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> loginUser(@RequestBody @Valid LoginUserRequest request,
                                                         HttpServletResponse response) {
        AuthTokens authTokens = authService.loginUser(
                new LoginUserDto(
                        request.email(),
                        request.password()
                )
        );

        Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authTokens.refreshToken());
        Cookie accessTokenCookie = cookieUtil.createAccessTokenCookie(authTokens.accessToken());

        response.addCookie(refreshTokenCookie);
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok(new AccessTokenResponse(authTokens.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "REFRESH-TOKEN") String refreshToken, HttpServletResponse response) {
        authService.logoutUser(refreshToken);

        response.addCookie(cookieUtil.invalidateAccessTokenCookie());
        response.addCookie(cookieUtil.invalidateRefreshTokenCookie());

        return ResponseEntity.ok().build();
    }

    @SecurityRequirement(name = "refreshToken")
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshTokens(
            @CookieValue(name = "REFRESH-TOKEN") String refreshToken, HttpServletResponse response) {
        AuthTokens authTokens = authService.refreshAccessToken(refreshToken);

        Cookie accessTokenCookie = cookieUtil.createAccessTokenCookie(authTokens.accessToken());
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/oauth2/link")
    public ResponseEntity<Void> linkOAuthAccounts(@RequestBody @Valid LinkOAuthAccountRequest linkOAuthAccountRequest,
                                                  HttpServletResponse response) {
        Optional<AuthTokens> authTokens = authService.linkNewOAuthAccount(new LinkOAuthAccountDto(
                linkOAuthAccountRequest.shouldLink(),
                linkOAuthAccountRequest.provider(),
                linkOAuthAccountRequest.providerId(),
                linkOAuthAccountRequest.userId()
        ));

        if (authTokens.isPresent()) {
            AuthTokens createdTokens = authTokens.get();

            Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(createdTokens.refreshToken());
            Cookie accessTokenCookie = cookieUtil.createAccessTokenCookie(createdTokens.accessToken());

            response.addCookie(refreshTokenCookie);
            response.addCookie(accessTokenCookie);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/oauth2/code/google")
    public ResponseEntity<?> handleGoogleCode(@RequestBody OAuthCodeRequest request,
                                              HttpServletResponse response) {
        GoogleLoginDTO googleLoginDTO = authService
                .handleGoogleLogin(new OAuthCodeDto(request.code(), request.codeVerifier()));

        return switch (googleLoginDTO.getLoginType()) {
            case NEW_USER, EXISTING_USER -> {
                Cookie refreshTokenCookie = cookieUtil
                        .createRefreshTokenCookie(googleLoginDTO.getRefreshToken());

                Cookie accessTokenCookie = cookieUtil
                        .createAccessTokenCookie(googleLoginDTO.getAccessToken());

                response.addCookie(refreshTokenCookie);
                response.addCookie(accessTokenCookie);

                yield ResponseEntity.ok().build();
            }
            case POSSIBLE_LINK -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new OAuth2LinkResponse(
                            googleLoginDTO.getProviderName(),
                            googleLoginDTO.getProviderId(),
                            googleLoginDTO.getUserId()
                    ));
        };
    }
}
