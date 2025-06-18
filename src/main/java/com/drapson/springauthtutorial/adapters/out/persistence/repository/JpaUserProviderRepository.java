package com.drapson.springauthtutorial.adapters.out.persistence.repository;

import com.drapson.springauthtutorial.adapters.out.persistence.entity.UserOAuthProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaUserProviderRepository extends JpaRepository<UserOAuthProviderEntity, UUID> {

    boolean existsByUserIdAndProvider(UUID userId, String provider);

}
