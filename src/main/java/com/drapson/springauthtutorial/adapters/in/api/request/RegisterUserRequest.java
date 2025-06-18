package com.drapson.springauthtutorial.adapters.in.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
        LocalDate birthDate,

        @NotNull
        boolean sendBudgetReports,

        @NotNull
        boolean isProfilePublic
) {
}
