package com.drapson.springauthtutorial.application.dtos;

public record AuthTokens(
    String accessToken,
    String refreshToken
) {
}
