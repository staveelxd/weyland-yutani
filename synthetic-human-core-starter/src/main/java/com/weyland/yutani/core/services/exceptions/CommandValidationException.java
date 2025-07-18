package com.weyland.yutani.core.services.exceptions;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Исключение, возникающее при неудачной проверке команды.
 * Содержит подробную информацию об ошибках валидации.
 */
@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommandValidationException extends RuntimeException {

    private final Map<String, String> validationErrors;

    /**
     * Создает новое исключение валидации команды с указанным сообщением.
     *
     * @param message детальное сообщение об ошибке
     */
    public CommandValidationException(String message) {
        super(message);
        this.validationErrors = Collections.emptyMap();
    }

    /**
     * Создает новое исключение валидации команды с указанным сообщением и ошибками валидации.
     *
     * @param message детальное сообщение об ошибке
     * @param validationErrors отображение имен полей в сообщения об ошибках
     */
    public CommandValidationException(String message, Map<String, String> validationErrors) {
        super(message);
        this.validationErrors = Objects.requireNonNullElse(validationErrors, Collections.emptyMap());
    }

    /**
     * Создает новое исключение CommandValidationException на основе набора нарушений ограничений.
     *
     * @param violations набор нарушений ограничений
     * @return новое исключение CommandValidationException с ошибками валидации
     */
    public static <T> CommandValidationException fromViolations(Set<ConstraintViolation<T>> violations) {
        if (violations == null || violations.isEmpty()) {
            return new CommandValidationException("Ошибка валидации");
        }

        Map<String, String> errors = violations.stream()
            .collect(java.util.stream.Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage,
                (existing, replacement) -> existing + "; " + replacement
            ));

        return new CommandValidationException("Ошибка валидации: " + errors, errors);
    }

    /**
     * Создает новое исключение CommandValidationException для ошибки в одном поле.
     *
     * @param field имя поля
     * @param message сообщение об ошибке
     * @return новое исключение CommandValidationException с ошибкой в одном поле
     */
    public static CommandValidationException forField(String field, String message) {
        return new CommandValidationException(
            String.format("Ошибка валидации поля '%s': %s", field, message),
            Collections.singletonMap(field, message)
        );
    }

    /**
     * Проверяет наличие ошибок валидации.
     *
     * @return true, если есть ошибки валидации, иначе false
     */
    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }
}
