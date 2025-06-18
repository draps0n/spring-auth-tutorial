package com.drapson.springauthtutorial.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> getUserByEmail(String email);

    User save(User user);

    List<User> getAllUsers();
}
