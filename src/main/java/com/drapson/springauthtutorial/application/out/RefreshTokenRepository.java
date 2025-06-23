package com.drapson.springauthtutorial.application.out;

import com.drapson.springauthtutorial.application.dtos.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);

    Optional<RefreshToken> getRefreshTokenByHashedToken(String hashedRefreshToken);

    void updateRevokedStatus(UUID id, boolean revoked);
}
