package com.drapson.springauthtutorial.adapters.in.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record LinkAccountsRequest(
        @NotNull
        String linkToken,

        @NotNull
        boolean shouldLinkAccounts
) {
}
