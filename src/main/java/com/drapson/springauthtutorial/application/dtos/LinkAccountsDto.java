package com.drapson.springauthtutorial.application.dtos;

public record LinkAccountsDto(
        String linkToken,
        boolean shouldLinkAccounts
) {

}
