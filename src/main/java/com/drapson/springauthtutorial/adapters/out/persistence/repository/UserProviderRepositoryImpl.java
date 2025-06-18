package com.drapson.springauthtutorial.adapters.out.persistence.repository;

import com.drapson.springauthtutorial.application.UserProviderRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserProviderRepositoryImpl implements UserProviderRepository {

    private final JpaUserProviderRepository jpaUserProviderRepository;

    public UserProviderRepositoryImpl(JpaUserProviderRepository jpaUserProviderRepository) {
        this.jpaUserProviderRepository = jpaUserProviderRepository;
    }

    @Override
    public boolean checkIfUserHasProvider(UUID id, String provider) {
        return jpaUserProviderRepository.existsByUserIdAndProvider(id, provider);
    }
}
