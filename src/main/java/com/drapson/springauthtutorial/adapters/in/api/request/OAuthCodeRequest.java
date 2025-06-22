package com.drapson.springauthtutorial.adapters.in.api.request;

public record OAuthCodeRequest(
    String code,
    String codeVerifier
) {
}
