package com.example.userservice.mapper;

import com.example.userservice.dto.CreateUserRequestDto;
import com.example.userservice.dto.UpdateUserRequestDto;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toEntity(CreateUserRequestDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .build();
    }

    public void updateEntityFromDto(UpdateUserRequestDto dto, User user) {
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }
    }
}