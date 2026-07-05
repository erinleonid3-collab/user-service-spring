package com.example.userservice.controller;

import com.example.userservice.dto.UserEventDto;
import com.example.userservice.service.EmailService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody EmailRequestDto request) {
        emailService.sendNotification(request.getEmail(), request.getEventType());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Email отправлен на " + request.getEmail()
        ));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmailRequestDto {
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
        private String email;

        @NotBlank(message = "Тип события не может быть пустым")
        private String eventType;
    }
}