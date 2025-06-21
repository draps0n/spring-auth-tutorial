package com.drapson.springauthtutorial.application.dtos;

public record LinkOAuthAccountDto(
        String linkToken,
        boolean shouldLinkAccounts
) {

}
