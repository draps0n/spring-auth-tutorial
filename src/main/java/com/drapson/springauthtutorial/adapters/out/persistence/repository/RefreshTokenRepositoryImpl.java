package com.drapson.springauthtutorial.adapters.out.persistence.repository;

import com.drapson.springauthtutorial.adapters.out.persistence.entity.RefreshTokenEntity;
import com.drapson.springauthtutorial.adapters.out.persistence.mapper.RefreshTokenMapper;
import com.drapson.springauthtutorial.application.RefreshTokenRepository;
import com.drapson.springauthtutorial.application.dtos.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;

    public RefreshTokenRepositoryImpl(JpaRefreshTokenRepository jpaRefreshTokenRepository) {
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
    }

    @Override
    public void save(RefreshToken refreshToken) {
        RefreshTokenEntity refreshTokenEntity = RefreshTokenMapper.toEntity(refreshToken);
        jpaRefreshTokenRepository.save(refreshTokenEntity);
    }

    @Override
    public Optional<RefreshToken> getRefreshTokenByHashedToken(String hashedRefreshToken) {
        return jpaRefreshTokenRepository
                .findByToken(hashedRefreshToken)
                .map(RefreshTokenMapper::toDomain);
    }

    @Override
    public void updateRevokedStatus(UUID id, boolean revoked) {
        jpaRefreshTokenRepository.updateRevokedById(id, revoked);
    }
}
