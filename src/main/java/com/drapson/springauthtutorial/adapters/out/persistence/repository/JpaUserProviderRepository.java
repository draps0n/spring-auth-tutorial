package com.drapson.springauthtutorial.adapters.out.persistence.repository;

import com.drapson.springauthtutorial.adapters.out.persistence.entity.UserOAuthProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserProviderRepository extends JpaRepository<UserOAuthProviderEntity, UUID> {

    boolean existsByUserIdAndProvider(UUID userId, String provider);

    @Query("""
                    SELECT u
                    FROM UserOAuthProviderEntity up
                    JOIN FETCH up.user u
                    WHERE up.provider = :providerName
                    AND up.providerId = :providerId
            """)
    Optional<UserOAuthProviderEntity> findByProviderAndProviderId(String providerName, String providerId);
}
