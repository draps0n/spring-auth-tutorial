package com.drapson.springauthtutorial.application.dtos;

import com.drapson.springauthtutorial.domain.User;

import java.util.UUID;

public record UserOAuthProvider(
        UUID id,
        String provider,
        String providerId,
        User user
) {
}
