package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.Operation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationEvent {

    @NotBlank(message = "Email не может быть пустым!")
    @Email(message = "Некорректный email")
    private String email;

    @NotNull(message = "Operation не может быть пустым")
    private Operation operation;


}