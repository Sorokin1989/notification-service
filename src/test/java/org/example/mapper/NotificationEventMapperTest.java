package org.example.mapper;


import org.example.dto.NotificationEventResponse;
import org.example.entity.NotificationEvent;
import org.example.enums.Operation;
import org.example.enums.Status;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class NotificationEventMapperTest {


        NotificationEventMapper mapper = Mappers.getMapper(NotificationEventMapper.class);


    @Test
    void toNotificationEventResponse_shouldMapAllFields(){
        NotificationEvent event = NotificationEvent.builder()
                .id(1L)
                .email("test@yandex.ru")
                .operation(Operation.CREATE)
                .status(Status.SENT)
                .createdAt(LocalDateTime.of(2026, 10, 20, 14, 0))
                .processedAt(LocalDateTime.of(2026, 10, 20, 14, 1))
                .build();

      NotificationEventResponse response= mapper.toNotificationEventResponse(event);

      assertThat(response).isNotNull();
      assertThat(response.getId()).isEqualTo(event.getId());
      assertThat(response.getEmail()).isEqualTo(event.getEmail());
      assertThat(response.getOperation()).isEqualTo(event.getOperation());
      assertThat(response.getStatus()).isEqualTo(event.getStatus());
      assertThat(response.getCreatedAt()).isEqualTo(event.getCreatedAt());
      assertThat(response.getProcessedAt()).isEqualTo(event.getProcessedAt());

    }
}
