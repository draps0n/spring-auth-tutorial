package com.drapson.springauthtutorial.adapters.in.security;

public class EmailLinkedToAnotherAccountWithDifferentProviderException extends RuntimeException {
    private final String linkToken;

    public EmailLinkedToAnotherAccountWithDifferentProviderException(String message, String linkToken) {
        super(message);
        this.linkToken = linkToken;
    }

    public String getLinkToken() {
        return linkToken;
    }
}
