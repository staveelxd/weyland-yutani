package com.weyland.yutani.core.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Command {

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @NotNull(message = "Команда не может не иметь приоритета")
    private Priority priority;

    @NotBlank(message = "Автор не может быть пустым")
    @Size(max = 100, message = "Автор не должен превышать 100 символов")
    private String author;

    @NotNull(message = "Время не может быть пустым")
    private LocalDateTime time;
}
