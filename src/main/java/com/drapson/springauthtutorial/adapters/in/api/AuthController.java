package com.drapson.springauthtutorial.adapters.in.api;

import com.drapson.springauthtutorial.adapters.in.api.request.LoginUserRequest;
import com.drapson.springauthtutorial.adapters.in.api.request.RegisterUserRequest;
import com.drapson.springauthtutorial.application.AuthService;
import com.drapson.springauthtutorial.application.dtos.AuthTokens;
import com.drapson.springauthtutorial.application.dtos.CreatedUser;
import com.drapson.springauthtutorial.application.dtos.LoginUserDto;
import com.drapson.springauthtutorial.application.dtos.RegisterUserDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Use POST on /register to register a user");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        CreatedUser createdUser = authService.registerUser(
                new RegisterUserDto(
                        request.email(),
                        request.password(),
                        request.username()
                )
        );
        return ResponseEntity
                .created(URI.create("/api/v1/users/" + createdUser.id()))
                .body("User registered successfully");
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

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokens> refreshTokens(@RequestHeader("Authorization") String authorizationHeader) {
        String refreshToken = authorizationHeader.replace("Bearer ", "");
        AuthTokens authTokens = authService.refreshTokens(refreshToken);
        return ResponseEntity.ok(authTokens);
    }

}
