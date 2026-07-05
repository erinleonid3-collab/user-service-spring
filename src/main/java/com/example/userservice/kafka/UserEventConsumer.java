package com.example.userservice.kafka;

import com.example.userservice.dto.UserEventDto;
import com.example.userservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "user-service-group")
    public void consumeUserEvent(UserEventDto event) {
        log.info("Получено событие из Kafka: {}", event);
        emailService.sendNotification(event.getEmail(), event.getEventType());
    }
}
