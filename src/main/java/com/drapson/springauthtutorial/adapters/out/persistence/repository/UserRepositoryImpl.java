package com.drapson.springauthtutorial.adapters.out.persistence.repository;


import com.drapson.springauthtutorial.domain.User;
import com.drapson.springauthtutorial.domain.UserRepository;
import com.drapson.springauthtutorial.adapters.out.persistence.entity.UserEntity;
import com.drapson.springauthtutorial.adapters.out.persistence.mapper.UserMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = UserMapper.toEntity(user);
        UserEntity createdUserEntity = jpaUserRepository.save(userEntity);
        return UserMapper.toDomain(createdUserEntity);
    }

    @Override
    public List<User> getAllUsers() {
        return jpaUserRepository
                .findAll()
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<User> getUserByEmailWithPassword(String email) {
        return jpaUserRepository.findByEmailWithPassword(email).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return jpaUserRepository.findById(id).map(UserMapper::toDomain);
    }

}
