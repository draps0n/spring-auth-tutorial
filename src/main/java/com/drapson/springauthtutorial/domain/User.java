package com.drapson.springauthtutorial.domain;

import java.time.LocalDate;
import java.util.UUID;

public class User {
    private UUID id;
    private String email;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private boolean sendBudgetReports;
    private boolean isProfilePublic;

    protected User() {
    }

    public User(UUID id, String email, String password, String username, String firstName, String lastName, LocalDate birthDate, boolean sendBudgetReports, boolean isProfilePublic) {
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
}
