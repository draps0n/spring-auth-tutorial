package com.drapson.springauthtutorial.adapters.in.api;

import com.drapson.springauthtutorial.adapters.in.api.request.*;
import com.drapson.springauthtutorial.application.AuthService;
import com.drapson.springauthtutorial.application.dtos.*;
import com.drapson.springauthtutorial.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirements
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthTokens> registerUser(@RequestBody @Valid RegisterUserRequest request) {
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

        return ResponseEntity.ok(authTokens);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokens> loginUser(@RequestBody @Valid LoginUserRequest request) {
        AuthTokens authTokens = authService.loginUser(
                new LoginUserDto(
                        request.email(),
                        request.password()
                )
        );
        return ResponseEntity.ok(authTokens);
    }

    @SecurityRequirement(name = "refreshToken")
    @PostMapping("/refresh")
    public ResponseEntity<AuthTokens> refreshTokens(@RequestHeader("Authorization") String authorizationHeader) {
        String refreshToken = authorizationHeader.replace("Bearer ", "");
        AuthTokens authTokens = authService.refreshTokens(refreshToken);
        return ResponseEntity.ok(authTokens);
    }

    @PostMapping("/finish-oauth-registration")
    public ResponseEntity<AuthTokens> finishOAuthRegistration(@RequestBody @Valid AdditionalRegistrationInfoRequest additionalRegistrationInfoRequest) {
        AuthTokens authTokens = authService.finishOAuthRegistration(new FinishOAuthRegistrationDto(
                additionalRegistrationInfoRequest.token(),
                additionalRegistrationInfoRequest.username(),
                additionalRegistrationInfoRequest.birthDate(),
                additionalRegistrationInfoRequest.sendBudgetReports(),
                additionalRegistrationInfoRequest.isProfilePublic()
        ));

        return ResponseEntity.ok(authTokens);
    }


    @PostMapping("/link-oauth")
    public ResponseEntity<AuthTokens> linkOAuthAccounts(@RequestBody @Valid LinkOAuthAccountRequest linkOAuthAccountRequest) {
        AuthTokens authTokens = authService.linkNewOAuthAccount(new LinkOAuthAccountDto(
                linkOAuthAccountRequest.linkToken(),
                linkOAuthAccountRequest.shouldLinkAccounts()
        ));
        return authTokens == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(authTokens);
    }

    @PostMapping("/link-local")
    public ResponseEntity<AuthTokens> linkLocalAccount(@RequestBody @Valid LinkLocalAccountRequest linkLocalAccountRequest) {
        AuthTokens authTokens = authService.linkNewLocalAccount(new LinkLocalAccountDto(
                linkLocalAccountRequest.linkToken(),
                linkLocalAccountRequest.shouldLinkAccounts()
        ));
        return authTokens == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(authTokens);
    }

}
