package com.drapson.springauthtutorial.adapters.out.persistence.repository;

import com.drapson.springauthtutorial.adapters.out.persistence.entity.UserOAuthProviderEntity;
import com.drapson.springauthtutorial.adapters.out.persistence.mapper.UserOAuthProviderMapper;
import com.drapson.springauthtutorial.application.UserProviderRepository;
import com.drapson.springauthtutorial.application.dtos.UserOAuthProvider;
import org.springframework.stereotype.Repository;

import java.util.Optional;
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

    @Override
    public void save(UserOAuthProvider userOAuthProvider) {
        jpaUserProviderRepository.save(UserOAuthProviderMapper.toEntity(userOAuthProvider));
    }

    @Override
    public Optional<UserOAuthProvider> getOAuthUserByProviderAndProviderId(String providerName, String providerId) {
        return jpaUserProviderRepository
                .findByProviderAndProviderId(providerName, providerId)
                .map(UserOAuthProviderMapper::toDomain);
    }
}
