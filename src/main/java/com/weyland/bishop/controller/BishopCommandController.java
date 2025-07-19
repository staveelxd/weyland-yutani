package com.weyland.bishop.controller;

import com.weyland.yutani.core.annotations.WeylandWatchingYou;
import com.weyland.yutani.core.model.Command;
import com.weyland.yutani.core.model.Priority;
import com.weyland.yutani.core.services.CommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/commands")
@RequiredArgsConstructor
public class BishopCommandController {

    private final CommandService commandService;

    @WeylandWatchingYou("Отправление команды")
    @PostMapping
    public ResponseEntity<?> submitCommand(@RequestBody CommandDto commandDto) {
        Command command = new Command(
                commandDto.description(),
                commandDto.priority(),
                commandDto.author(),
                commandDto.time() != null ? commandDto.time() : LocalDateTime.now()
        );

        var result = commandService.processCommand(command);
        return ResponseEntity.accepted().body(result);
    }

    @GetMapping("/queue-status")
    public ResponseEntity<?> getQueueStatus() {
        return ResponseEntity.ok(commandService.getQueueStatus());
    }

    public record CommandDto(
            String description,
            Priority priority,
            String author,
            LocalDateTime time
    ) {}
}