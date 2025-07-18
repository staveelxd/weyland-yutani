package com.weyland.yutani.core.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class MetricsService {

    private final MeterRegistry registry;
    private final Map<String, AtomicInteger> processedByAuthor = new ConcurrentHashMap<>();
    private final AtomicLong queueOverflowCount = new AtomicLong(0);
    private final AtomicLong processedCommandsCount = new AtomicLong(0);
    private final AtomicInteger currentQueueSize = new AtomicInteger(0);

    private static final String QUEUE_SIZE_METRIC = "synthetic_human.queue.size";
    private static final String PROCESSED_COMMANDS_METRIC = "synthetic_human.commands.processed";
    private static final String QUEUE_OVERFLOW_METRIC = "synthetic_human.queue.overflow";
    private static final String COMMANDS_BY_AUTHOR_METRIC = "synthetic_human.commands.by_author";

    public MetricsService(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    public void init() {
        Gauge.builder(QUEUE_SIZE_METRIC, currentQueueSize, AtomicInteger::get)
            .description("Текущее количество команд в очереди")
            .register(registry);
        Counter.builder(PROCESSED_COMMANDS_METRIC)
            .description("Общее количество обработанных команд")
            .register(registry);
        Counter.builder(QUEUE_OVERFLOW_METRIC)
            .description("Количество случаев переполнения очереди команд")
            .register(registry);
        log.info("Metrics service initialized");
    }

    /**
     * Обновить метрику текущего размера очереди
     * @param size Текущий размер очереди
     */
    public void updateQueueSize(int size) {
        currentQueueSize.set(size);
    }

    /**
     * Увеличить счетчик команд, обработанных определенным автором
     * @param author Автор команды
     */
    public void incrementProcessedByAuthor(String author) {
        processedByAuthor.computeIfAbsent(author, k -> new AtomicInteger(0)).incrementAndGet();
        processedCommandsCount.incrementAndGet();

        // Обновить счетчик для этого автора
        Counter.builder(COMMANDS_BY_AUTHOR_METRIC)
            .tags(Tags.of(Tag.of("author", author)))
            .register(registry)
            .increment();
    }

    /**
     * Зарегистрировать событие переполнения очереди
     */
    public void incrementQueueOverflow() {
        queueOverflowCount.incrementAndGet();
        registry.counter(QUEUE_OVERFLOW_METRIC).increment();
    }

    /**
     * Получить количество команд, обработанных каждым автором
     * @return Map авторов и количества команд
     */
    public Map<String, Integer> getProcessedByAuthor() {
        Map<String, Integer> result = new java.util.HashMap<>();
        processedByAuthor.forEach((author, count) -> result.put(author, count.get()));
        return result;
    }

    /**
     * Получить текущий размер очереди
     * @return Текущий размер очереди
     */
    public long getQueueOverflowCount() {
        return queueOverflowCount.get();
    }

    public long getProcessedCommandsCount() {
        return processedCommandsCount.get();
    }
}
