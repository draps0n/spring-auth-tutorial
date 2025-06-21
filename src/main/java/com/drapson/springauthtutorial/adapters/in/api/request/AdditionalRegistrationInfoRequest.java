package com.drapson.springauthtutorial.adapters.in.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AdditionalRegistrationInfoRequest(
        @NotBlank
        String token,

        @Size(min = 3, max = 20)
        @NotBlank
        String username,

        @NotNull
        @Past
        LocalDate birthDate,

        @NotNull
        boolean sendBudgetReports,

        @NotNull
        boolean isProfilePublic
) {
}
