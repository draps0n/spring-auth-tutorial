package com.drapson.springauthtutorial.adapters.out.persistence.mapper;

import com.drapson.springauthtutorial.adapters.out.persistence.entity.UserOAuthProviderEntity;
import com.drapson.springauthtutorial.application.dtos.UserOAuthProvider;

public class UserOAuthProviderMapper {

    public static UserOAuthProviderEntity toEntity(UserOAuthProvider userOAuthProvider) {
        return new UserOAuthProviderEntity(
                userOAuthProvider.id(),
                userOAuthProvider.provider(),
                userOAuthProvider.providerId(),
                UserMapper.toEntity(userOAuthProvider.user())
        );
    }

    public static UserOAuthProvider toDomain(UserOAuthProviderEntity userOAuthProviderEntity) {
        return new UserOAuthProvider(
                userOAuthProviderEntity.getId(),
                userOAuthProviderEntity.getProvider(),
                userOAuthProviderEntity.getProviderId(),
                UserMapper.toDomain(userOAuthProviderEntity.getUser())
        );
    }

}
