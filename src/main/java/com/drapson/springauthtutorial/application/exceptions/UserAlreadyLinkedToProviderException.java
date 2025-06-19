package com.drapson.springauthtutorial.application.exceptions;

public class UserAlreadyLinkedToProviderException extends RuntimeException {
    public UserAlreadyLinkedToProviderException(String message)
    {
        super(message);
    }
}
