package com.drapson.springauthtutorial.adapters.out.persistence.mapper;

import com.drapson.springauthtutorial.domain.User;
import com.drapson.springauthtutorial.adapters.out.persistence.entity.UserEntity;

public class UserMapper {
    public static UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.isSendBudgetReports(),
                user.isProfilePublic()
        );
    }

    public static User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getUsername(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDate(),
                userEntity.isSendBudgetReports(),
                userEntity.isProfilePublic()
        );
    }
}
