package com.drapson.springauthtutorial.domain;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserByEmail(String email);

    User save(User user);

    List<User> getAllUsers();

    Optional<User> findByEmail(String email);

    Optional<User> getUserByEmailWithoutPassword(String email);

    Optional<User> getUserByEmailWithPassword(String email);
}
