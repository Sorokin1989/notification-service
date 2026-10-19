package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper; // <-- Используем ObjectMapper
import org.example.dto.UserNotificationEvent;
import org.example.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationService notificationService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Test
    void whenPostAccountEvent_thenCallServiceAndReturn200() throws Exception {
        UserNotificationEvent event = new UserNotificationEvent();
        event.setEmail("test@yandex.ru");
        event.setOperation(UserNotificationEvent.Operation.CREATE);

        mockMvc.perform(post("/api/notifications/account-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event))) // Используем objectMapper
                .andExpect(status().isOk());

        verify(notificationService).sendAccountEvent(any(UserNotificationEvent.class));
    }
}