package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserRequestDto;
import com.example.userservice.dto.UpdateUserRequestDto;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.exception.UserAlreadyExistsException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Тесты UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users - должен создать пользователя")
    void createUser_ShouldReturn201() throws Exception {
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .name("Иван Иванов")
                .email("ivan@example.com")
                .age(25)
                .build();

        UserResponseDto response = UserResponseDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@example.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.createUser(any(CreateUserRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));

        verify(userService, times(1)).createUser(any());
    }

    @Test
    @DisplayName("POST /api/users - должен вернуть 400 при невалидных данных")
    void createUser_WithInvalidData_ShouldReturn400() throws Exception {
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .name("")
                .email("invalid-email")
                .age(-5)
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());

        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("GET /api/users/{id} - должен вернуть пользователя")
    void getUserById_ShouldReturnUser() throws Exception {
        UserResponseDto response = UserResponseDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@example.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван Иванов"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - должен вернуть 404, если не найден")
    void getUserById_NotFound_ShouldReturn404() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new UserNotFoundException("Пользователь с ID 999 не найден"));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с ID 999 не найден"));
    }

    @Test
    @DisplayName("GET /api/users - должен вернуть список пользователей")
    void getAllUsers_ShouldReturnList() throws Exception {
        List<UserResponseDto> users = List.of(
                UserResponseDto.builder().id(1L).name("Иван").email("ivan@test.com").age(25).build(),
                UserResponseDto.builder().id(2L).name("Петр").email("petr@test.com").age(30).build()
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Иван"))
                .andExpect(jsonPath("$[1].name").value("Петр"));
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - должен обновить пользователя")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UpdateUserRequestDto request = UpdateUserRequestDto.builder()
                .age(26)
                .build();

        UserResponseDto response = UserResponseDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@example.com")
                .age(26)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.updateUser(eq(1L), any(UpdateUserRequestDto.class))).thenReturn(response);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(26));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - должен удалить пользователя")
    void deleteUser_ShouldReturn204() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("POST /api/users - должен вернуть 409 при дубликате email")
    void createUser_WithDuplicateEmail_ShouldReturn409() throws Exception {
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .name("Иван")
                .email("ivan@example.com")
                .age(25)
                .build();

        when(userService.createUser(any()))
                .thenThrow(new UserAlreadyExistsException("Email уже занят"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email уже занят"));
    }
}