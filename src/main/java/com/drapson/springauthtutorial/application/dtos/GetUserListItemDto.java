package com.drapson.springauthtutorial.application.dtos;

public record GetUserListItemDto(
        String email,
        String password,
        String username
) {
}
