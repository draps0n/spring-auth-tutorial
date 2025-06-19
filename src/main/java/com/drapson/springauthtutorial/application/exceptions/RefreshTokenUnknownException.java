package com.drapson.springauthtutorial.application.exceptions;

public class RefreshTokenUnknownException extends RuntimeException {
    public RefreshTokenUnknownException(String message) {
        super(message);
    }
}
