package com.drapson.springauthtutorial.adapters.in.api.request;

import jakarta.validation.constraints.NotNull;

public record LinkAccountsRequest(
        @NotNull
        String linkToken,

        @NotNull
        boolean shouldLinkAccounts,

        AdditionalRegistrationInfoRequest additionalRegistrationInfoRequest
) {
}
