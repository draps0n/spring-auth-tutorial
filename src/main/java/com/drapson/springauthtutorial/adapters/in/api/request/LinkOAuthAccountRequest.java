package com.drapson.springauthtutorial.adapters.in.api.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LinkOAuthAccountRequest(
        @NotNull
        boolean shouldLink,

        @NotNull
        String provider,

        @NotNull
        String providerId,

        @NotNull
        UUID userId
) {
}
