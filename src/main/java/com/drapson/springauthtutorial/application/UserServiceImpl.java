package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.GetUserListItemDto;
import com.drapson.springauthtutorial.domain.User;
import com.drapson.springauthtutorial.domain.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<GetUserListItemDto> getAllUsers() {
        return userRepository
                .getAllUsers()
                .stream()
                .map(
                        user -> new GetUserListItemDto(
                                user.getEmail(),
                                user.getPassword(),
                                user.getUsername()
                        )
                )
                .toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
