package com.drapson.springauthtutorial.application.dtos;

public record OAuthCodeDto(
    String code,
    String codeVerifier
) {
}
