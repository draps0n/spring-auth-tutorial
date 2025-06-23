package com.drapson.springauthtutorial.application.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LinkOAuthAccountDto(
        boolean shouldLink,
        String provider,
        String providerId,
        UUID userId
) {

}
