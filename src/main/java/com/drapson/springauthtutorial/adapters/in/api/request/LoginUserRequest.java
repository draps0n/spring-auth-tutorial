package com.drapson.springauthtutorial.adapters.in.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
}
