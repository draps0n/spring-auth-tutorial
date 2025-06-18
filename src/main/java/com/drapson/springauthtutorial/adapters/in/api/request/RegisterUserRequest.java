package com.drapson.springauthtutorial.adapters.in.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @Email
        @NotBlank
        String email,

        @Size(min = 8)
        @NotBlank
        String password,

        @Size(min = 3, max = 20)
        @NotBlank
        String username) {
}
