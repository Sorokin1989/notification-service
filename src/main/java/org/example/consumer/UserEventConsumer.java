package org.example.consumer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserNotificationEvent;
import org.example.service.EmailService;
import org.example.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "user-notifications", groupId = "notification-service-group")
    public void consume(@Valid UserNotificationEvent event) {
        log.info("Received event: {} for {}", event.getOperation(), event.getEmail());
        notificationService.sendAccountEvent(event);

    }
}