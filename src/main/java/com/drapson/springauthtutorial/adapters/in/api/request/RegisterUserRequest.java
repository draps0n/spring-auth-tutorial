package com.drapson.springauthtutorial.adapters.in.api.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterUserRequest(
        @Email
        @NotBlank
        String email,

        @Size(min = 8)
        @NotBlank
        String password,

        @Size(min = 3, max = 20)
        @NotBlank
        String username,

        @Size(min = 5, max = 50)
        @NotBlank
        String firstName,

        @Size(min = 5, max = 50)
        @NotBlank
        String lastName,

        @NotNull
        @Past
        LocalDate birthDate,

        @NotNull
        boolean sendBudgetReports,

        @NotNull
        boolean isProfilePublic
) {
}
