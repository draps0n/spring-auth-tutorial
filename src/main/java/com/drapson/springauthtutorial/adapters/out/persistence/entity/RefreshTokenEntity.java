package com.drapson.springauthtutorial.adapters.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@DynamicUpdate
public class RefreshTokenEntity {
    @Id
    private UUID id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Version
    private long version;

    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity(UUID id, String token, UserEntity user, LocalDateTime issuedAt, LocalDateTime expiresAt, boolean revoked) {
        this.id = id;
        this.token = token;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.user = user;
        this.revoked = revoked;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
