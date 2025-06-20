package com.drapson.springauthtutorial.adapters.in.api;

import com.drapson.springauthtutorial.adapters.in.api.request.*;
import com.drapson.springauthtutorial.application.AuthService;
import com.drapson.springauthtutorial.application.dtos.*;
import com.drapson.springauthtutorial.application.exceptions.RefreshTokenNotProvidedException;
import com.drapson.springauthtutorial.domain.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AuthToken> registerUser(@RequestBody @Valid RegisterUserRequest request,
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
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new AuthToken(authTokens.accessToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthToken> loginUser(@RequestBody @Valid LoginUserRequest request,
                                               HttpServletResponse response) {
        AuthTokens authTokens = authService.loginUser(
                new LoginUserDto(
                        request.email(),
                        request.password()
                )
        );
        Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authTokens.refreshToken());
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new AuthToken(authTokens.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "REFRESH-TOKEN", required = false, defaultValue = "") String refreshToken, HttpServletResponse response){

        if (refreshToken.isBlank()) {
            throw new RefreshTokenNotProvidedException("Refresh token not provided in cookies!");
        }
        authService.refreshTokens(refreshToken);

        Cookie refreshTokenCookie = CookieUtil.invalidateCookie();
        response.addCookie(refreshTokenCookie);

        authService.logoutUser(refreshToken);
        return ResponseEntity.ok().build();
    }

    @SecurityRequirement(name = "refreshToken")
    @PostMapping("/refresh")
    public ResponseEntity<AuthToken> refreshTokens(
            @CookieValue(name = "REFRESH-TOKEN", required = false, defaultValue = "") String refreshToken) {

        if (refreshToken.isBlank()) {
            throw new RefreshTokenNotProvidedException("Refresh token not provided in cookies!");
        }

        AuthTokens authTokens = authService.refreshTokens(refreshToken);
        return ResponseEntity.ok(new AuthToken(authTokens.accessToken()));
    }

    @PostMapping("/finish-oauth-registration")
    public ResponseEntity<AuthToken> finishOAuthRegistration(@RequestBody @Valid AdditionalRegistrationInfoRequest additionalRegistrationInfoRequest,
                                                             HttpServletResponse response) {
        AuthTokens authTokens = authService.finishOAuthRegistration(new FinishOAuthRegistrationDto(
                additionalRegistrationInfoRequest.token(),
                additionalRegistrationInfoRequest.username(),
                additionalRegistrationInfoRequest.birthDate(),
                additionalRegistrationInfoRequest.sendBudgetReports(),
                additionalRegistrationInfoRequest.isProfilePublic()
        ));

        Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authTokens.refreshToken());
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new AuthToken(authTokens.accessToken()));
    }

    @PostMapping("/link-oauth")
    public ResponseEntity<AuthToken> linkOAuthAccounts(@RequestBody @Valid LinkOAuthAccountRequest linkOAuthAccountRequest,
                                                       HttpServletResponse response) {
        AuthTokens authTokens = authService.linkNewOAuthAccount(new LinkOAuthAccountDto(
                linkOAuthAccountRequest.linkToken(),
                linkOAuthAccountRequest.shouldLinkAccounts()
        ));

        if (authTokens != null) {
            Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authTokens.refreshToken());
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(new AuthToken(authTokens.accessToken()));
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/link-local")
    public ResponseEntity<AuthToken> linkLocalAccount(@RequestBody @Valid LinkLocalAccountRequest linkLocalAccountRequest,
                                                      HttpServletResponse response) {
        AuthTokens authTokens = authService.linkNewLocalAccount(new LinkLocalAccountDto(
                linkLocalAccountRequest.linkToken(),
                linkLocalAccountRequest.shouldLinkAccounts()
        ));
        if (authTokens != null) {
            Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authTokens.refreshToken());
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(new AuthToken(authTokens.accessToken()));
        }
        return ResponseEntity.noContent().build();
    }
}
