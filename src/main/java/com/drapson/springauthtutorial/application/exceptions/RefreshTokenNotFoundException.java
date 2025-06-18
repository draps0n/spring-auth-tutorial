package com.drapson.springauthtutorial.application.exceptions;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
