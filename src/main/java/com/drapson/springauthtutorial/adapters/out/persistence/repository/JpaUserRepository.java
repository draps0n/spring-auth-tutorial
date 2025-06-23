package com.drapson.springauthtutorial.adapters.out.persistence.repository;

import com.drapson.springauthtutorial.adapters.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.password IS NOT NULL")
    Optional<UserEntity> findByEmailWithPassword(String email);

    Optional<UserEntity> findByEmail(String email);
}
