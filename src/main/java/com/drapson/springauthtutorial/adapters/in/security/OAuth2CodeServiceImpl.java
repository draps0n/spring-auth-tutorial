package com.drapson.springauthtutorial.adapters.in.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.drapson.springauthtutorial.application.out.OAuth2CodeService;
import com.drapson.springauthtutorial.application.dtos.GoogleUserDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class OAuth2CodeServiceImpl implements OAuth2CodeService {

    private final WebClient webClient;
    private final String googleClientId;
    private final String googleClientSecret;

    public OAuth2CodeServiceImpl(WebClient webClient, String googleClientId, String googleClientSecret) {
        this.webClient = webClient;
        this.googleClientId = googleClientId;
        this.googleClientSecret = googleClientSecret;
    }

    @Override
    public Map<String, String> exchangeCodeForTokens(String code, String codeVerifier) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("client_id", googleClientId);
        formData.add("client_secret", googleClientSecret);
        formData.add("redirect_uri", "http://localhost:4200/oauth2/callback");
        formData.add("code_verifier", codeVerifier);

        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .block();
    }

    @Override
    public GoogleUserDto extractUserInfoFromIdToken(String idToken) {
        DecodedJWT jwt = JWT.decode(idToken);

        return new GoogleUserDto(
                jwt.getClaim("sub").asString(),
                jwt.getClaim("email").asString(),
                jwt.getClaim("given_name").asString(),
                jwt.getClaim("family_name").asString()
        );
    }

}
