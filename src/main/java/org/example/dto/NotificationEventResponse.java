package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.Operation;
import org.example.enums.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEventResponse {

    private Long id;
    private String email;
    private Operation operation;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}