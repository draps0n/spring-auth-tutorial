package com.drapson.springauthtutorial.application.dtos;

import java.util.UUID;

public record AuthTokens(
    String accessToken,
    String refreshToken
) {
}
