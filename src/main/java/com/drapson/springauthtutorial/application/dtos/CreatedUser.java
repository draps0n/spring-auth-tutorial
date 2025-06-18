package com.drapson.springauthtutorial.application.dtos;

import java.util.UUID;

public record CreatedUser(
        UUID id,
        String email,
        String username
) {
}
