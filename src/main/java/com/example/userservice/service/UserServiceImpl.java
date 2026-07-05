package com.example.userservice.service;

import com.example.userservice.dto.CreateUserRequestDto;
import com.example.userservice.dto.UpdateUserRequestDto;
import com.example.userservice.dto.UserEventDto;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.entity.User;
import com.example.userservice.exception.UserAlreadyExistsException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.kafka.UserEventProducer;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventProducer userEventProducer;

    @Override
    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Пользователь с email " + dto.getEmail() + " уже существует"
            );
        }

        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);

        UserEventDto event = UserEventDto.builder()
                .email(saved.getEmail())
                .eventType("CREATED")
                .timestamp(LocalDateTime.now().toString())
                .build();
        userEventProducer.sendUserEvent(event);

        return userMapper.toResponseDto(saved);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));
        return userMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UpdateUserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new UserAlreadyExistsException(
                        "Email " + dto.getEmail() + " уже занят"
                );
            }
        }

        userMapper.updateEntityFromDto(dto, user);
        User updated = userRepository.save(user);
        return userMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));

        String email = user.getEmail();
        userRepository.deleteById(id);

        // Отправляем событие в Kafka
        UserEventDto event = UserEventDto.builder()
                .email(email)
                .eventType("DELETED")
                .timestamp(LocalDateTime.now().toString())
                .build();
        userEventProducer.sendUserEvent(event);
    }
}