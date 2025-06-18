package com.drapson.springauthtutorial.application.dtos;

public record RegisterUserDto(
        String email,
        String password,
        String username
) {
}
