package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.*;
import com.drapson.springauthtutorial.domain.User;

import java.util.Optional;

public interface AuthService {
    User registerUser(RegisterUserDto registerUserDto);

    AuthTokens loginUser(LoginUserDto loginUserDto);

    void logoutUser(String token);

    AuthTokens refreshAccessToken(String refreshToken);

    AuthTokens issueJwtTokens(User user);

    boolean checkPassword(String providedPassword, String hashedPassword);
}
