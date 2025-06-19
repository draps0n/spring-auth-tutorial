package com.drapson.springauthtutorial.application.exceptions;

public class AccessTokenExpiredException extends RuntimeException {
    public AccessTokenExpiredException(String message) {
        super(message);
    }
}
