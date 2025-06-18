package com.drapson.springauthtutorial.application.dtos;

public record LoginUserDto(
        String email,
        String password
) {
}
