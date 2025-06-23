package com.drapson.springauthtutorial.adapters.in.api.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LinkOAuthAccountRequest(
        @NotNull
        boolean shouldLink,

        String provider,

        String providerId,

        UUID userId,

        String password
) {
        public boolean validate() {
                return !shouldLink || provider != null && providerId != null && userId != null && password != null;
        }
}
