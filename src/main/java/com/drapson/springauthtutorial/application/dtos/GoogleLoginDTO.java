package com.drapson.springauthtutorial.application.dtos;

public record GoogleLoginDTO(
        boolean isNewUser,
        String accessToken,
        String refreshToken,
        String providerId,
        String providerName,
        String email
) {

}
