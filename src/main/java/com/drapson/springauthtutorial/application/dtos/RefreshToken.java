package com.drapson.springauthtutorial.application.dtos;

import com.drapson.springauthtutorial.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefreshToken(
        UUID id,
        String token,
        User user,
        LocalDateTime issuedAt,
        LocalDateTime expiresAt,
        boolean revoked
) {}

