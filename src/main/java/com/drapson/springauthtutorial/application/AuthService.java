package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.AuthTokens;
import com.drapson.springauthtutorial.application.dtos.CreatedUser;
import com.drapson.springauthtutorial.application.dtos.LoginUserDto;
import com.drapson.springauthtutorial.application.dtos.RegisterUserDto;
import com.drapson.springauthtutorial.domain.User;

public interface AuthService {
    CreatedUser registerUser(RegisterUserDto registerUserDto);

    AuthTokens loginUser(LoginUserDto loginUserDto);

    void logoutUser(String token);

    AuthTokens refreshTokens(String refreshToken);

    AuthTokens issueJwtTokens(User user);

    boolean checkIfUserHasProvider(User user, String provider);
}
