package com.example.userservice.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Интеграционные тесты EmailService")
class EmailServiceIntegrationTest {

    private GreenMail greenMail;

    @Autowired
    private EmailService emailService;

    @BeforeAll
    void startMailServer() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    @AfterAll
    void stopMailServer() {
        if (greenMail != null) {
            greenMail.stop();
        }
    }

    @BeforeEach
    void resetMailServer() {
        greenMail.reset();
    }

    @DynamicPropertySource
    static void configureMailProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", () -> ServerSetupTest.SMTP.getBindAddress());
        registry.add("spring.mail.port", () -> ServerSetupTest.SMTP.getPort());
    }

    @Test
    @DisplayName("Должен отправить email о создании аккаунта")
    void sendNotification_Created_ShouldSendEmail() throws MessagingException {
        emailService.sendNotification("test@example.com", "CREATED");

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);

        MimeMessage message = messages[0];
        assertThat(message.getSubject()).isEqualTo("Добро пожаловать!");
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Должен отправить email об удалении аккаунта")
    void sendNotification_Deleted_ShouldSendEmail() throws MessagingException {
        emailService.sendNotification("test@example.com", "DELETED");

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);

        MimeMessage message = messages[0];
        assertThat(message.getSubject()).isEqualTo("Аккаунт удалён");
    }

    @Test
    @DisplayName("Должен игнорировать неизвестный тип события")
    void sendNotification_UnknownEvent_ShouldNotSendEmail() {
        emailService.sendNotification("test@example.com", "UNKNOWN");

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).isEmpty();
    }
}