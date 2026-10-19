package org.example.consumer;

import org.example.dto.UserNotificationEvent;
import org.example.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JacksonJsonSerializer"
})
class UserEventConsumerTest {

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("apache/kafka:4.1.0")
    );

    @Autowired
    private KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void whenCreateEvent_thenSendCreatedNotification() throws Exception {
        UserNotificationEvent event = new UserNotificationEvent();
        event.setEmail("test@yandex.ru");
        event.setOperation(UserNotificationEvent.Operation.CREATE);

        kafkaTemplate.send("user-notifications", event).get(5, TimeUnit.SECONDS);

        verify(notificationService, timeout(5000)).sendAccountEvent(any(UserNotificationEvent.class));
    }

    @Test
    void whenDeleteEvent_thenSendDeletedNotification() throws Exception {
        UserNotificationEvent event = new UserNotificationEvent();
        event.setEmail("test@yandex.ru");
        event.setOperation(UserNotificationEvent.Operation.DELETE);

        kafkaTemplate.send("user-notifications", event).get(5, TimeUnit.SECONDS);

        verify(notificationService, timeout(5000)).sendAccountEvent(any(UserNotificationEvent.class));
    }
}