package com.drapson.springauthtutorial.application.exceptions;

public class LinkedUserNotFoundException extends RuntimeException {
    public LinkedUserNotFoundException(String message) {
        super(message);
    }
}
