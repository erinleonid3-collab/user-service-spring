package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserRequestDto;
import com.example.userservice.dto.UpdateUserRequestDto;
import com.example.userservice.dto.UserDtoWithLinks;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Создать нового пользователя", description = "Создаёт пользователя и отправляет событие в Kafka")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
    })
    public ResponseEntity<UserDtoWithLinks> createUser(
            @Parameter(description = "Данные для создания пользователя")
            @Valid @RequestBody CreateUserRequestDto dto) {
        UserResponseDto created = userService.createUser(dto);
        UserDtoWithLinks dtoWithLinks = userService.toDtoWithLinks(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoWithLinks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает данные пользователя с HATEOAS ссылками")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<UserDtoWithLinks> getUserById(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        UserDtoWithLinks dtoWithLinks = userService.toDtoWithLinks(user);
        return ResponseEntity.ok(dtoWithLinks);
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей с HATEOAS ссылками")
    @ApiResponse(responseCode = "200", description = "Список пользователей")
    public ResponseEntity<CollectionModel<UserDtoWithLinks>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        List<UserDtoWithLinks> dtosWithLinks = users.stream()
                .map(userService::toDtoWithLinks)
                .toList();

        CollectionModel<UserDtoWithLinks> collection = CollectionModel.of(dtosWithLinks);
        collection.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        collection.add(linkTo(methodOn(UserController.class).createUser(null)).withRel("create-user"));

        return ResponseEntity.ok(collection);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить данные пользователя", description = "Частичное обновление данных пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Email уже занят")
    })
    public ResponseEntity<UserDtoWithLinks> updateUser(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Данные для обновления")
            @Valid @RequestBody UpdateUserRequestDto dto) {
        UserResponseDto updated = userService.updateUser(id, dto);
        UserDtoWithLinks dtoWithLinks = userService.toDtoWithLinks(updated);
        return ResponseEntity.ok(dtoWithLinks);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя и отправляет событие в Kafka")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}