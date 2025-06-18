package com.drapson.springauthtutorial.application;

import com.drapson.springauthtutorial.application.dtos.GetUserListItemDto;

import java.util.List;

public interface UserService {
    List<GetUserListItemDto> getAllUsers();
}
