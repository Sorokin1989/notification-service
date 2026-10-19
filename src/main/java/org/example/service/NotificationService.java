package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserNotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;


    @Value("${email.message.subject.create-user}")
    private String createSubject;

    @Value("${email.message.text.create-user}")
    private String createText;

    @Value("${email.message.subject.delete-user}")
    private String deleteSubject;

    @Value("${email.message.text.delete-user}")
    private String deleteText;


    public void sendAccountEvent(UserNotificationEvent userNotificationEvent){
        if (userNotificationEvent.getOperation() == UserNotificationEvent.Operation.CREATE) {
           sendAccountCreated(userNotificationEvent.getEmail());
        } else if (userNotificationEvent.getOperation() == UserNotificationEvent.Operation.DELETE) {
            sendAccountDeleted(userNotificationEvent.getEmail());
        }
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
