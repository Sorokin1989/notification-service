package org.example.service;

import org.example.dto.UserNotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "createSubject", "Account Created");
        ReflectionTestUtils.setField(notificationService, "createText", "Hello! Your account...");
        ReflectionTestUtils.setField(notificationService, "deleteSubject", "Account Deleted");
        ReflectionTestUtils.setField(notificationService, "deleteText", "Hello! Your account has been deleted.");
    }

    @Test
    void sendAccountEvent_whenCreate_thenSendCreatedEmail() {
        UserNotificationEvent event = new UserNotificationEvent(
                "test@yandex.ru", UserNotificationEvent.Operation.CREATE);

        notificationService.sendAccountEvent(event);

        verify(emailService).send("test@yandex.ru", "Account Created", "Hello! Your account...");
    }

    @Test
    void sendAccountEvent_whenDelete_thenSendDeletedEmail() {
        UserNotificationEvent event = new UserNotificationEvent(
                "test@yandex.ru", UserNotificationEvent.Operation.DELETE);

        notificationService.sendAccountEvent(event);

        verify(emailService).send("test@yandex.ru", "Account Deleted", "Hello! Your account has been deleted.");
    }
}