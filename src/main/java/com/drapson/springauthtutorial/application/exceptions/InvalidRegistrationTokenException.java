package com.drapson.springauthtutorial.application.exceptions;

public class InvalidRegistrationTokenException extends RuntimeException {
    public InvalidRegistrationTokenException(String message) {
        super(message);
    }
}
