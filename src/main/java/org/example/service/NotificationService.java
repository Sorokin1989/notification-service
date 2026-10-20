package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.NotificationEventResponse;
import org.example.dto.UserNotificationEvent;
import org.example.entity.NotificationEvent;
import org.example.enums.Operation;
import org.example.enums.Status;
import org.example.mapper.NotificationEventMapper;
import org.example.repository.EventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final EventRepository eventRepository;
    private final NotificationEventMapper notificationEventMapper;


    @Value("${email.message.subject.create-user}")
    private String createSubject;

    @Value("${email.message.text.create-user}")
    private String createText;

    @Value("${email.message.subject.delete-user}")
    private String deleteSubject;

    @Value("${email.message.text.delete-user}")
    private String deleteText;


    public NotificationEventResponse sendAccountEvent(UserNotificationEvent userNotificationEvent) {

        NotificationEvent event = NotificationEvent.builder()
                .email(userNotificationEvent.getEmail())
                .operation(userNotificationEvent.getOperation())
                .status(Status.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        event = eventRepository.save(event);


        try {
            if (userNotificationEvent.getOperation() == Operation.CREATE) {
                sendAccountCreated(userNotificationEvent.getEmail());

            } else if (userNotificationEvent.getOperation() == Operation.DELETE) {
                sendAccountDeleted(userNotificationEvent.getEmail());
            }
            event.setStatus(Status.SENT);
            event.setProcessedAt(LocalDateTime.now());
            eventRepository.save(event);
        } catch (Exception e) {
            event.setStatus(Status.FAILED);
            event.setProcessedAt(LocalDateTime.now());
            eventRepository.save(event);
            throw e;
        }

        return notificationEventMapper.toNotificationEventResponse(event);

    }

    void sendAccountCreated(String email) {
        emailService.send(email, createSubject,
                createText);
    }

    void sendAccountDeleted(String email) {
        emailService.send(email, deleteSubject,
                deleteText);
    }

}
