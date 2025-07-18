package com.weyland.yutani.core.services.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int status;
    private String message;
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    private Map<String, Object> details;

    /**
     * Создает новый объект ErrorResponse с указанными параметрами.
     *
     * @param status HTTP статус-код
     * @param message Сообщение об ошибке
     * @param path Путь к эндпоинту API
     * @param timestamp Когда произошла ошибка
     */
    public ErrorResponse(int status, String message, String path, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }
    
    /**
     * Создает новый объект ErrorResponse с указанными параметрами и деталями.
     *
     * @param status HTTP статус-код
     * @param message Сообщение об ошибке
     * @param path Путь к эндпоинту API
     * @param timestamp Когда произошла ошибка
     * @param details Дополнительные детали ошибки
     */
    public ErrorResponse(int status, String message, String path, 
                        LocalDateTime timestamp, Map<String, Object> details) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.details = details;
    }

    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    public static class ErrorResponseBuilder {
        private int status;
        private String message;
        private String path;
        private LocalDateTime timestamp = LocalDateTime.now();
        private Map<String, Object> details;
        
        public ErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }
        public ErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
        public ErrorResponseBuilder path(String path) {
            this.path = path;
            return this;
        }
        public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        public ErrorResponseBuilder details(Map<String, Object> details) {
            this.details = details;
            return this;
        }
        public ErrorResponse build() {
            return new ErrorResponse(status, message, path, timestamp, details);
        }
    }
}
