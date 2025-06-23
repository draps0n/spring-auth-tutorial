package com.drapson.springauthtutorial.adapters.in.api.response;

public record GoogleAuthResponse(
        boolean isNewUser,
        String providerId,
        String providerName,
        String email
) {
}
