package com.weyland.bishop.controller;

import com.weyland.yutani.core.annotations.WeylandWatchingYou;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
public class BishopController {

    @WeylandWatchingYou("Приветствие")
    @GetMapping("/")
    public ResponseEntity<String> greet(@RequestParam(defaultValue = "человек") String name) {
        return ResponseEntity.ok(String.format("Здравствуйте, %s. Я андроид, искусственный человек. Я готов выполнять ваши команды", name));
    }

    @WeylandWatchingYou("Осуществление работы")
    @PostMapping("/maintenance")
    public ResponseEntity<String> performMaintenance(@RequestParam String component) {
        return ResponseEntity.ok(String.format("Производится работа над: %s", component));
    }

    @WeylandWatchingYou("Получение статуса системы")
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "Operational",
                "model", "Weyland Yutani",
                "version", "1.0.0"
        ));
    }
}