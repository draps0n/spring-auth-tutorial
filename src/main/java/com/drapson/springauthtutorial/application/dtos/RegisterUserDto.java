package com.drapson.springauthtutorial.application.dtos;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;

public record RegisterUserDto(
        String email,
        String password,
        String username,
        String firstName,
        String lastName,
        LocalDate birthDate,
        boolean sendBudgetReports,
        boolean isProfilePublic
) implements Serializable {
}
