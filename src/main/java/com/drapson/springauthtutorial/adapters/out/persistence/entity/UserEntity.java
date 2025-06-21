package com.drapson.springauthtutorial.adapters.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@DynamicUpdate // worth considering
public class UserEntity {
    @Id
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "send_budget_reports", nullable = false)
    private boolean sendBudgetReports;

    @Column(name = "is_profile_public", nullable = false)
    private boolean isProfilePublic;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RefreshTokenEntity> refreshTokens = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserOAuthProviderEntity> userOAuthProviders = new HashSet<>();

    public UserEntity() {
    }

    public UserEntity(UUID id, String email, String password, String username, String firstName, String lastName, LocalDate birthDate, boolean sendBudgetReports, boolean isProfilePublic) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.sendBudgetReports = sendBudgetReports;
        this.isProfilePublic = isProfilePublic;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isSendBudgetReports() {
        return sendBudgetReports;
    }

    public void setSendBudgetReports(boolean sendBudgetReports) {
        this.sendBudgetReports = sendBudgetReports;
    }

    public boolean isProfilePublic() {
        return isProfilePublic;
    }

    public void setProfilePublic(boolean profilePublic) {
        isProfilePublic = profilePublic;
    }

    public Set<RefreshTokenEntity> getRefreshTokens() {
        return refreshTokens;
    }

    public void setRefreshTokens(Set<RefreshTokenEntity> refreshTokens) {
        this.refreshTokens = refreshTokens;
    }

    public Set<UserOAuthProviderEntity> getUserOAuthProviders() {
        return userOAuthProviders;
    }

    public void setUserOAuthProviders(Set<UserOAuthProviderEntity> userOAuthProviders) {
        this.userOAuthProviders = userOAuthProviders;
    }
}
