package com.weyland.yutani.core.services;

import com.weyland.yutani.core.services.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandlerService {

    public static final int HTTP_TOO_MANY_REQUESTS = 429;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    public static final int HTTP_UNPROCESSABLE_ENTITY = 422; // Для ошибок валидации

    @ExceptionHandler(CommandQueueOverflowException.class)
    public ResponseEntity<ErrorResponse> handleQueueOverflow(CommandQueueOverflowException ex, WebRequest request) {
        log.warn("Переполнение очереди: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            HTTP_TOO_MANY_REQUESTS,
            "Очередь команд переполнена. Пожалуйста, повторите попытку позже.",
            request.getDescription(false),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
    }
    
    @ExceptionHandler(CommandValidationException.class)
    public ResponseEntity<ErrorResponse> handleCommandValidation(CommandValidationException ex, WebRequest request) {
        log.warn("Ошибка валидации команды: {}", ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("validationErrors", ex.getValidationErrors());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HTTP_UNPROCESSABLE_ENTITY,
            ex.getMessage(),
            request.getDescription(false),
            LocalDateTime.now(),
            details
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    @ExceptionHandler(CommandExecutionException.class)
    public ResponseEntity<ErrorResponse> handleCommandExecution(CommandExecutionException ex, WebRequest request) {
        log.error("Ошибка выполнения команды: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            request.getDescription(false),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                    FieldError::getField,
                error -> error.getDefaultMessage() == null ? "Неизвестное значение" : error.getDefaultMessage()
            ));

        ErrorResponse errorResponse = new ErrorResponse(
            HTTP_UNPROCESSABLE_ENTITY,
            "Request validation failed",
            request.getDescription(false),
            LocalDateTime.now(),
            Map.of("validationErrors", errors)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                violation -> violation.getMessage() == null ? "Invalid value" : violation.getMessage()
            ));

        ErrorResponse errorResponse = new ErrorResponse(
            HTTP_UNPROCESSABLE_ENTITY,
            "Не удалось выполнить валидацию",
            request.getDescription(false),
            LocalDateTime.now(),
            Map.of("validationErrors", errors)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    @ExceptionHandler({
        IllegalArgumentException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, WebRequest request) {
        log.warn("Неверный параметр запроса: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Неверный параметр запроса: " + ex.getMessage(),
            request.getDescription(false),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex, WebRequest request) {
        log.error("Непредвиденная ошибка: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
            HTTP_INTERNAL_SERVER_ERROR,
            "Произошла непредвиденная ошибка. Пожалуйста, повторите попытку позже.",
            request.getDescription(false),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
