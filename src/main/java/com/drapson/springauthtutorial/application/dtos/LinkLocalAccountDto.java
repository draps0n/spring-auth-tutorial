package com.drapson.springauthtutorial.application.dtos;

public record LinkLocalAccountDto(
        String linkToken,
        boolean shouldLinkAccounts
) {
}
