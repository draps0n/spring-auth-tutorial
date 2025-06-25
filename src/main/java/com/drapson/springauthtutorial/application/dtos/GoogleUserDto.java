package com.drapson.springauthtutorial.application.dtos;

public record GoogleUserDto(
        String sub,
        String email,
        String firstName,
        String lastName
) {
}
