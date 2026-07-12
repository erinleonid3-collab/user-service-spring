package com.example.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendNotification(String email, String eventType) {
        String subject;
        String text;

        if ("CREATED".equals(eventType)) {
            subject = "Добро пожаловать!";
            text = "Здравствуйте! Ваш аккаунт успешно создан.";
        } else if ("DELETED".equals(eventType)) {
            subject = "Аккаунт удалён";
            text = "Здравствуйте! Ваш аккаунт удалён.";
        } else {
            log.warn("Неизвестный тип события: {}", eventType);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("noreply@mywebsite.com");

        mailSender.send(message);
        log.info("Email отправлен на {} с темой: {}", email, subject);
    }
}