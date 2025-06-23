package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.AuthTokens;
import com.drapson.springauthtutorial.application.dtos.GoogleLoginDTO;
import com.drapson.springauthtutorial.application.dtos.LinkOAuthAccountDto;
import com.drapson.springauthtutorial.application.dtos.OAuthCodeDto;

import java.util.Optional;

public interface OAuth2Service {

    Optional<AuthTokens> linkNewOAuthAccount(LinkOAuthAccountDto linkOAuthAccountDto);

    GoogleLoginDTO handleGoogleLogin(OAuthCodeDto oAuthCodeDto);

}
