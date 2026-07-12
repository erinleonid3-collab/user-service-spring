package com.example.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для обновления данных пользователя")
public class UpdateUserRequestDto {

    @Schema(description = "Новое имя пользователя", example = "Петр Петров")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String name;

    @Schema(description = "Новый email пользователя", example = "petr@example.com")
    @Email(message = "Некорректный формат email")
    private String email;

    @Schema(description = "Новый возраст пользователя", example = "30")
    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 150, message = "Возраст не может быть больше 150")
    private Integer age;
}