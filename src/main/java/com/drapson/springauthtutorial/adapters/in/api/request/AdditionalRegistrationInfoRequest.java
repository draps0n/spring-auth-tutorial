package com.drapson.springauthtutorial.adapters.in.api.request;

import java.time.LocalDate;

public record AdditionalRegistrationInfoRequest(
        String username,
        LocalDate birthDate,
        boolean sendBudgetReports,
        boolean isProfilePublic
) {
}
