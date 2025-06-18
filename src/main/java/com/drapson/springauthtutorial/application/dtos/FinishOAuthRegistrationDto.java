package com.drapson.springauthtutorial.application.dtos;

import java.time.LocalDate;

public record FinishOAuthRegistrationDto(
        String token,
        String username,
        LocalDate birthDate,
        boolean sendBudgetReports,
        boolean isProfilePublic
) {
}
