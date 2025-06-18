package com.drapson.springauthtutorial.application.dtos;

import java.io.Serializable;

public record PendingOAuthRegistration(
    String provider,
    String providerId,
    String email,
    String firstName,
    String lastName
) implements Serializable {
}
