package com.drapson.springauthtutorial.adapters.in.security;

public class AdditionalRegistrationInfoNeededException extends RuntimeException {

    private final String registrationToken;

    public AdditionalRegistrationInfoNeededException(String message, String registrationToken) {
        super(message);
        this.registrationToken = registrationToken;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }
}
