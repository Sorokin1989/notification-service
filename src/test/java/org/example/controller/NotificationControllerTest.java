package org.example.controller;

import org.example.dto.NotificationEventResponse;
import org.example.dto.UserNotificationEvent;
import org.example.enums.Operation;
import org.example.enums.Status;
import org.example.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper; // Jackson 3

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void whenPostAccountEvent_thenReturnResponseAndCallService() throws Exception {
        UserNotificationEvent event = new UserNotificationEvent();
        event.setEmail("test@yandex.ru");
        event.setOperation(Operation.CREATE);

        NotificationEventResponse response = NotificationEventResponse.builder()
                .id(1L)
                .email("test@yandex.ru")
                .operation(Operation.CREATE)
                .status(Status.SENT)
                .createdAt(LocalDateTime.now())
                .processedAt(LocalDateTime.now())
                .build();

        when(notificationService.sendAccountEvent(any(UserNotificationEvent.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/notifications/account-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@yandex.ru"))
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.operation").value("CREATE"));

        verify(notificationService).sendAccountEvent(any(UserNotificationEvent.class));
    }

    @Test
    void whenInvalidJson_thenReturn400() throws Exception {
        mockMvc.perform(post("/api/notifications/account-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Некорректный JSON в теле запроса"));
    }

    @Test
    void whenMissingEmail_thenReturn400WithErrorsMap() throws Exception {
        String json = """
                {"operation": "CREATE"}
                """;
        mockMvc.perform(post("/api/notifications/account-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации данных"))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void whenWrongHttpMethod_thenReturn405() throws Exception {
        mockMvc.perform(get("/api/notifications/account-event"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").value(containsString("Метод не поддерживается")));
    }

    @Test
    void whenUnknownUrl_thenReturn404() throws Exception {
        mockMvc.perform(get("/api/notifications/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ресурс не найден"));
    }

    @Test
    void whenIllegalArgument_thenReturn400WithoutLeakingDetails() throws Exception {
        doThrow(new IllegalArgumentException("Секретное техническое сообщение"))
                .when(notificationService).sendAccountEvent(any());

        UserNotificationEvent event = new UserNotificationEvent();
        event.setEmail("test@yandex.ru");
        event.setOperation(Operation.CREATE);

        mockMvc.perform(post("/api/notifications/account-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Некорректный аргумент запроса"))
                .andExpect(jsonPath("$.message").value(not(containsString("Секретное"))));
    }

    @Test
    void whenServiceThrowsUnexpectedException_thenReturn500() throws Exception {
        doThrow(new RuntimeException("Неожиданная ошибка"))
                .when(notificationService).sendAccountEvent(any());

        UserNotificationEvent event = new UserNotificationEvent();
        event.setEmail("test@yandex.ru");
        event.setOperation(Operation.CREATE);

        mockMvc.perform(post("/api/notifications/account-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Ошибка сервера. Попробуйте позже!"));
    }
}