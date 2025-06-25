package com.drapson.springauthtutorial.application.dtos;

import java.util.UUID;

public record LinkOAuthAccountDto(
        boolean shouldLink,
        String provider,
        String providerId,
        UUID userId,
        String password
) {

}
