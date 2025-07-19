package com.weyland.yutani.core.web;

import com.weyland.yutani.core.model.Command;
import com.weyland.yutani.core.model.Priority;
import com.weyland.yutani.core.services.CommandService;
import com.weyland.yutani.core.services.MetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/commands")
@Tag(name = "Команды", description = "API для отправки команд и проверки статуса очереди")
public class CommandController {

    private final CommandService commandService;
    private final MetricsService metricsService;

    /**
     * Отправить новую команду на обработку
     *
     * @param commandDto Команда для обработки
     * @return Ответ, указывающий статус отправки команды
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Отправить новую команду",
        description = "Отправляет новую команду на обработку. Критические команды обрабатываются немедленно, " +
                     "а обычные команды добавляются в очередь обработки.",
        responses = {
            @ApiResponse(
                responseCode = "202",
                description = "Команда принята на обработку",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Неверные данные команды"
            ),
            @ApiResponse(
                responseCode = "429",
                description = "Очередь команд переполнена"
            )
        }
    )
    public ResponseEntity<CommandResponse> submitCommand(@Valid @RequestBody CommandDto commandDto) {
        log.debug("Получен запрос на отправку команды: {}", commandDto);
        Command command = new Command(
            commandDto.description(),
            commandDto.priority(),
            commandDto.author(),
            commandDto.time() != null ? commandDto.time() : LocalDateTime.now()
        );
        Map<String, Object> result = commandService.processCommand(command);
        CommandResponse response = new CommandResponse(
            (String) result.get("status"),
            command,
            (Integer) result.getOrDefault("queueSize", 0)
        );
        return ResponseEntity.accepted().body(response);
    }

    /**
     * Получить текущий статус очереди команд
     *
     * @return Map, содержащая информацию о статусе очереди
     */
    @GetMapping(value = "/queue-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Получить статус очереди команд",
        description = "Возвращает текущий статус очереди обработки команд",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Статус очереди успешно получен",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)
                )
            )
        }
    )
    public ResponseEntity<QueueStatusResponse> getQueueStatus() {
        Map<String, Object> status = commandService.getQueueStatus();
        QueueStatusResponse response = new QueueStatusResponse(
            (Integer) status.get("queueSize"),
            (Long) status.get("processedCommands"),
            (Map<String, Integer>) status.get("processedByAuthor"),
            (Long) status.get("queueOverflowCount"),
            (LocalDateTime) status.get("timestamp")
        );
        return ResponseEntity.ok(response);
    }

    public record CommandDto(
        @Schema(description = "Описание команды", example = "Проверить состояние энергоблока")
        String description,
        @Schema(description = "Приоритет выполнения команды", example = "CRITICAL")
        Priority priority,
        @Schema(description = "Автор команды", example = "Лейтенант Эллен Рипли")
        String author,
        @Schema(description = "Время назначения команды (ISO-8601 format)",
                example = "2025-07-17T12:00:00Z")
        LocalDateTime time
    ) {}

    public record CommandResponse(
        @Schema(description = "Статус команды",
                example = "command_queued", 
                allowableValues = {"critical_command_executed", "command_queued"})
        String status,
        @Schema(description = "Отправленная команда")
        Command command,
        @Schema(description = "Текущий размер очереди")
        int queueSize
    ) {}

    public record QueueStatusResponse(
        @Schema(description = "Текущее количество команд в очереди")
        int queueSize,
        @Schema(description = "Количество выполненных команд")
        long processedCommands,
        @Schema(description = "Разбиение выполненных команд по авторам")
        Map<String, Integer> processedByAuthor,
        @Schema(description = "Количество случаев переполнения очереди")
        long queueOverflowCount,
        @Schema(description = "Время проверки статуса")
        LocalDateTime timestamp
    ) {}
}

