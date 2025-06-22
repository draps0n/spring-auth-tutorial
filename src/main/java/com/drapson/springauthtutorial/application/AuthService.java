package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.*;
import com.drapson.springauthtutorial.domain.User;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    User registerUser(RegisterUserDto registerUserDto);

    AuthTokens loginUser(LoginUserDto loginUserDto);

    void logoutUser(String token);

    AuthTokens refreshAccessToken(String refreshToken);

    AuthTokens issueJwtTokens(User user);

    boolean checkIfUserHasProvider(User user, String provider);

    AuthTokens finishOAuthRegistration(FinishOAuthRegistrationDto finishOAuthRegistrationDto);

    AuthTokens linkNewOAuthAccount(LinkOAuthAccountDto linkOAuthAccountDto);

    AuthTokens linkNewLocalAccount(LinkLocalAccountDto linkLocalAccountDto);

    String issueTemporaryRegistrationToken(PendingOAuthRegistration pendingOAuthRegistration);

    void handleGoogleLogin(OAuthCodeDto oAuthCodeDto);
}
