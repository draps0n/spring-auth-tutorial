package com.drapson.springauthtutorial.adapters.in.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OAuthCodeRequest(
        @NotBlank(message = "provider is required")
        String code,

        @NotBlank(message = "codeVerifier is required")
        String codeVerifier
) {
}
