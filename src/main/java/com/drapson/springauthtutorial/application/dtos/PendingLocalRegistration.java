package com.drapson.springauthtutorial.application.dtos;

import java.io.Serializable;

public record PendingLocalRegistration(
        String email,
        String hashedPassword
) implements Serializable {
}
