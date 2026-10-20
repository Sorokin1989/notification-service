package org.example.service;

import org.example.dto.NotificationEventResponse;
import org.example.dto.UserNotificationEvent;
import org.example.entity.NotificationEvent;
import org.example.enums.Operation;
import org.example.enums.Status;
import org.example.mapper.NotificationEventMapper;
import org.example.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private NotificationEventMapper mapper;

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
        UserNotificationEvent event = new UserNotificationEvent("test@yandex.ru", Operation.CREATE);

        when(eventRepository.save(any(NotificationEvent.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toNotificationEventResponse(any(NotificationEvent.class)))
                .thenReturn(new NotificationEventResponse());

        notificationService.sendAccountEvent(event);

        verify(emailService).send("test@yandex.ru", "Account Created", "Hello! Your account...");
    }

    @Test
    void sendAccountEvent_whenDelete_thenSendDeletedEmail() {
        UserNotificationEvent event = new UserNotificationEvent("test@yandex.ru", Operation.DELETE);

        when(eventRepository.save(any(NotificationEvent.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toNotificationEventResponse(any(NotificationEvent.class)))
                .thenReturn(new NotificationEventResponse());

        notificationService.sendAccountEvent(event);

        verify(emailService).send("test@yandex.ru", "Account Deleted", "Hello! Your account has been deleted.");
    }

    @Test
    void sendAccountEvent_thenSaveTwiceWithPendingAndSent() {
        UserNotificationEvent event = new UserNotificationEvent("test@yandex.ru", Operation.CREATE);

        List<Status> savedStatuses = new ArrayList<>();
        when(eventRepository.save(any(NotificationEvent.class)))
                .thenAnswer(inv -> {
                    NotificationEvent e = inv.getArgument(0);
                    savedStatuses.add(e.getStatus());
                    return e;
                });
        when(mapper.toNotificationEventResponse(any(NotificationEvent.class)))
                .thenReturn(new NotificationEventResponse());

        notificationService.sendAccountEvent(event);

        assertEquals(2, savedStatuses.size());
        assertEquals(Status.PENDING, savedStatuses.get(0));
        assertEquals(Status.SENT, savedStatuses.get(1));
    }

    @Test
    void sendAccountEvent_whenEmailFails_thenStatusFailed() {
        UserNotificationEvent event = new UserNotificationEvent("test@yandex.ru", Operation.CREATE);

        List<Status> savedStatuses = new ArrayList<>();
        when(eventRepository.save(any(NotificationEvent.class)))
                .thenAnswer(inv -> {
                    NotificationEvent e = inv.getArgument(0);
                    savedStatuses.add(e.getStatus());
                    return e;
                });

        doThrow(new RuntimeException("SMTP down"))
                .when(emailService).send(anyString(), anyString(), anyString());

        try {
            notificationService.sendAccountEvent(event);
        } catch (RuntimeException ignored) {

        }

        assertEquals(2, savedStatuses.size());
        assertEquals(Status.PENDING, savedStatuses.get(0));
        assertEquals(Status.FAILED, savedStatuses.get(1));
    }
}