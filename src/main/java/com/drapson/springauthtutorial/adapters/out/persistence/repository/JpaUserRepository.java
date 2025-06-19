package com.drapson.springauthtutorial.adapters.out.persistence.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.drapson.springauthtutorial.adapters.out.persistence.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.password IS NULL")
    Optional<UserEntity> findByEmailWithoutPassword(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.password IS NOT NULL")
    Optional<UserEntity> findByEmailWithPassword(String email);
}
