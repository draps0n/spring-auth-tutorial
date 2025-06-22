package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.GoogleUserDto;

import java.util.Map;

public interface OAuth2CodeService {

    Map<String, String> exchangeCodeForTokens(String code, String codeVerifier);

    GoogleUserDto extractUserInfoFromIdToken(String idToken);

}
