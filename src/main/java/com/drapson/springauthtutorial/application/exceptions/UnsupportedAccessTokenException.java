package com.drapson.springauthtutorial.application.exceptions;

public class UnsupportedAccessTokenException extends RuntimeException {
    public UnsupportedAccessTokenException(String message) {
        super(message);
    }
}
