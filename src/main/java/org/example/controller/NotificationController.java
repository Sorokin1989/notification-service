package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NotificationEventResponse;
import org.example.dto.UserNotificationEvent;
import org.example.service.EmailService;
import org.example.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/account-event")
    @ResponseStatus(HttpStatus.OK)
    public NotificationEventResponse notifyAccountEvent(@Valid @RequestBody UserNotificationEvent notificationEvent) {
        log.info("Received event: {} for {}", notificationEvent.getOperation(), notificationEvent.getEmail());
        return notificationService.sendAccountEvent(notificationEvent);
    }
}
