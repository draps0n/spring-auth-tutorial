package com.drapson.springauthtutorial.application.dtos;

import java.time.LocalDate;

public record LinkAccountsDto(
        String linkToken,
        boolean shouldLinkAccounts,
        String username,
        LocalDate birthDate,
        boolean sendBudgetReports,
        boolean isProfilePublic
) {

}
