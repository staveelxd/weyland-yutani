package com.weyland.yutani.core.services;

import com.weyland.yutani.core.model.Command;
import com.weyland.yutani.core.services.exceptions.CommandQueueOverflowException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Slf4j
@Service
public class CommandQueueService {
    @Getter
    private final int maxQueueSize;
    private final long processingDelayMs;
    private final MetricsService metricsService;
    private final BlockingQueue<Command> queue;
    private final ScheduledExecutorService executor;
    private volatile boolean isRunning;

    public CommandQueueService(
            @Value("${command.queue.max-size:100}") int maxQueueSize,
            @Value("${command.queue.processing-delay-ms:100}") long processingDelayMs,
            MetricsService metricsService) {
        this.maxQueueSize = maxQueueSize;
        this.processingDelayMs = processingDelayMs;
        this.metricsService = metricsService;
        this.queue = new LinkedBlockingQueue<>(maxQueueSize);
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.isRunning = false;
    }

    @PostConstruct
    public void init() {
        this.isRunning = true;
        scheduleNextProcess();
        log.info("Сервис очереди команд инициализирован с максимальным размером: {} и задержкой обработки: {} мс", 
                maxQueueSize, processingDelayMs);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Завершение работы сервиса очереди команд...");
        isRunning = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void addCommand(Command command) {
        if (!queue.offer(command)) {
            throw new CommandQueueOverflowException(
                String.format("Очередь команд переполнена! Максимальный размер: %d", maxQueueSize)
            );
        }
        log.debug("Команда добавлена в очередь: {}", command);
    }

    private void scheduleNextProcess() {
        if (!isRunning) {
            return;
        }
        
        executor.schedule(
            this::processNextCommand,
            processingDelayMs,
            TimeUnit.MILLISECONDS
        );
    }

    private void processNextCommand() {
        try {
            Command command = queue.poll();
            if (command != null) {
                log.info("Обработка команды: {}", command);
                metricsService.incrementProcessedByAuthor(command.getAuthor());
                log.info("Команда обработана: {}", command);
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке команды: {}", e.getMessage(), e);
        } finally {
            if (isRunning) {
                scheduleNextProcess();
            }
        }
    }

    public int getQueueSize() {
        return queue.size();
    }

}
