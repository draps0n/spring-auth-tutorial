package com.drapson.springauthtutorial.application.exceptions;

public class EmailLinkedThroughProviderException extends RuntimeException {
    public EmailLinkedThroughProviderException(String message) {
        super(message);
    }
}
