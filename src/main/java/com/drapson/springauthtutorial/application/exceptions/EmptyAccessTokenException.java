package com.drapson.springauthtutorial.application.exceptions;

public class EmptyAccessTokenException extends RuntimeException {
    public EmptyAccessTokenException(String message) {
        super(message);
    }
}
