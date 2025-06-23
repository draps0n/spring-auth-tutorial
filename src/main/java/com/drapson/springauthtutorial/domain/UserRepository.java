package com.drapson.springauthtutorial.domain;

import aj.org.objectweb.asm.commons.Remapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    List<User> getAllUsers();

    Optional<User> getUserByEmailWithoutPassword(String email);

    Optional<User> getUserByEmailWithPassword(String email);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(UUID id);
}
