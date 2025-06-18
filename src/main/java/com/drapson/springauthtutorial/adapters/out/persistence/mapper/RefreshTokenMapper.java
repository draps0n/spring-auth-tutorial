package com.drapson.springauthtutorial.adapters.out.persistence.mapper;

import com.drapson.springauthtutorial.adapters.out.persistence.entity.RefreshTokenEntity;
import com.drapson.springauthtutorial.application.dtos.RefreshToken;

public class RefreshTokenMapper {
    public static RefreshTokenEntity toEntity(RefreshToken refreshToken) {
        return new RefreshTokenEntity(
                refreshToken.id(),
                refreshToken.token(),
                UserMapper.toEntity(refreshToken.user()),
                refreshToken.issuedAt(),
                refreshToken.expiresAt(),
                refreshToken.revoked()
        );
    }

    public static RefreshToken toDomain(RefreshTokenEntity refreshTokenEntity) {
        return new RefreshToken(
                refreshTokenEntity.getId(),
                refreshTokenEntity.getToken(),
                UserMapper.toDomain(refreshTokenEntity.getUser()),
                refreshTokenEntity.getIssuedAt(),
                refreshTokenEntity.getExpiresAt(),
                refreshTokenEntity.isRevoked()
        );
    }
}
