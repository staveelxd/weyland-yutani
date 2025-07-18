package com.weyland.yutani.core.services;

import com.weyland.yutani.core.annotations.WeylandWatchingYou;
import com.weyland.yutani.core.model.Command;
import com.weyland.yutani.core.model.Priority;
import com.weyland.yutani.core.services.exceptions.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CommandService {

    private static final int MAX_AUTHOR_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;

    private final CommandQueueService queueService;
    private final MetricsService metricsService;

    private void validateCommand(Command command) {
        Map<String, String> errors = new HashMap<>();

        if (command.getAuthor() == null || command.getAuthor().trim().isEmpty()) {
            errors.put("author", "Автор не может быть пустым");
        } else if (command.getAuthor().length() > MAX_AUTHOR_LENGTH) {
            errors.put("author", String.format("Автор не может превышать %d символов", MAX_AUTHOR_LENGTH));
        }

        if (command.getDescription() == null || command.getDescription().trim().isEmpty()) {
            errors.put("description", "Описание не может быть пустым");
        } else if (command.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            errors.put("description", 
                String.format("Описание не может превышать %d символов", MAX_DESCRIPTION_LENGTH));
        }

        if (command.getTime() == null) {
            errors.put("time", "Время не может быть пустым");
        } else if (command.getTime().isAfter(LocalDateTime.now())) {
            errors.put("time", "Время не может быть в будущем");
        }

        if (!errors.isEmpty()) {
            throw new CommandValidationException("Ошибка валидации команды", errors);
        }
    }

    @WeylandWatchingYou("Обработка команды")
    public Map<String, Object> processCommand(@Valid Command command) {
        Objects.requireNonNull(command, "Команда не может быть пустой");
        validateCommand(command);
        try {
            if (command.getPriority() == Priority.CRITICAL) {
                log.info("Обработка CRITICAL команды: {}", command);
                metricsService.incrementProcessedByAuthor(command.getAuthor());
                try {
                    executeCriticalCommand(command);
                    String commandId = UUID.randomUUID().toString();
                    return Map.of(
                        "status", "critical_command_executed",
                        "commandId", commandId,
                        "commandDescription", command.getDescription(),
                        "timestamp", LocalDateTime.now()
                    );
                } catch (Exception e) {
                    throw CommandExecutionException.forCommand("unknown", e.getMessage());
                }
            } else {
                try {
                    queueService.addCommand(command);
                    int currentQueueSize = queueService.getQueueSize();
                    metricsService.updateQueueSize(currentQueueSize);
                    log.info("Команда поставлена в очередь. Текущий размер очереди: {}", currentQueueSize);
                    String commandId = UUID.randomUUID().toString();
                    return Map.of(
                        "status", "command_queued",
                        "commandId", commandId,
                        "commandDescription", command.getDescription(),
                        "queueSize", currentQueueSize,
                        "timestamp", LocalDateTime.now()
                    );
                } catch (CommandQueueOverflowException e) {
                    metricsService.incrementQueueOverflow();
                    log.warn("Переполнение очереди команд. Максимальный размер: {}", queueService.getMaxQueueSize());
                    throw e;
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке команды: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void executeCriticalCommand(Command command) {
        try {
            log.info("Выполнение критической команды: {}", command);
            if (command.getDescription() != null && command.getDescription().contains("fail")) {
                throw new IllegalStateException("Ошибка выполнения команды: ????????");
            }
        } catch (Exception e) {
            throw new CommandExecutionException("Ошибка выполнения критической команды: " + e.getMessage(), e);
        }
    }

    @WeylandWatchingYou("Получение статуса очереди")
    public Map<String, Object> getQueueStatus() {
        return Map.of(
            "queueSize", queueService.getQueueSize(),
            "processedCommands", metricsService.getProcessedCommandsCount(),
            "processedByAuthor", metricsService.getProcessedByAuthor(),
            "queueOverflowCount", metricsService.getQueueOverflowCount(),
            "timestamp", LocalDateTime.now()
        );
    }
}
