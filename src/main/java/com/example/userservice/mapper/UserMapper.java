package com.example.userservice.mapper;

import com.example.userservice.dto.CreateUserRequestDto;
import com.example.userservice.dto.UpdateUserRequestDto;
import com.example.userservice.dto.UserDtoWithLinks;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.entity.User;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

    public UserDtoWithLinks toDtoWithLinks(User user) {
        UserDtoWithLinks dto = new UserDtoWithLinks();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setCreatedAt(user.getCreatedAt());

        dto.add(linkTo(methodOn(com.example.userservice.controller.UserController.class)
                .getUserById(user.getId())).withSelfRel());

        dto.add(linkTo(methodOn(com.example.userservice.controller.UserController.class)
                .getAllUsers()).withRel("all-users"));

        dto.add(linkTo(methodOn(com.example.userservice.controller.UserController.class)
                .updateUser(user.getId(), null)).withRel("update"));

        dto.add(linkTo(methodOn(com.example.userservice.controller.UserController.class)
                .deleteUser(user.getId())).withRel("delete"));

        return dto;
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