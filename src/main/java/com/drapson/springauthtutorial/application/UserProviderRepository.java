package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.UserOAuthProvider;

import java.util.Optional;
import java.util.UUID;

public interface UserProviderRepository {
    boolean checkIfUserHasProvider(UUID id, String provider);

    void save(UserOAuthProvider userOAuthProvider);

    Optional<UserOAuthProvider> getOAuthUserByProviderAndProviderId(String providerName, String providerId);
}
