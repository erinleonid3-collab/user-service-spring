package com.example.userservice.kafka;

import com.example.userservice.dto.UserEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private static final String TOPIC = "user-events";

    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    public void sendUserEvent(UserEventDto event) {
        log.info("Отправка события в Kafka: {}", event);
        kafkaTemplate.send(TOPIC, event.getEmail(), event);
    }
}
