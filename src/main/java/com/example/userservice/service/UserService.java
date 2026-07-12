package com.example.userservice.service;

import com.example.userservice.dto.CreateUserRequestDto;
import com.example.userservice.dto.UpdateUserRequestDto;
import com.example.userservice.dto.UserDtoWithLinks;
import com.example.userservice.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(CreateUserRequestDto dto);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(Long id, UpdateUserRequestDto dto);
    void deleteUser(Long id);
    UserDtoWithLinks toDtoWithLinks(UserResponseDto dto);
}