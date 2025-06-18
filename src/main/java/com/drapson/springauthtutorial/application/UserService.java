package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.GetUserListItemDto;
import com.drapson.springauthtutorial.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<GetUserListItemDto> getAllUsers();

    Optional<User> findByEmail(String email);
}
