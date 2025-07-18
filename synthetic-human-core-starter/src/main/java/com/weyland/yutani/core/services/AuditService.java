package com.weyland.yutani.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weyland.yutani.core.annotations.WeylandWatchingYou;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditService {

    @Value("${audit.mode:CONSOLE}")
    private AuditMode mode = AuditMode.CONSOLE;
    @Value("${audit.kafka.topic:audit}")
    private String kafkaTopic;
    @Value("${audit.enabled:true}")
    private boolean enabled;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Around("@annotation(weylandWatchingYou)")
    public Object audit(ProceedingJoinPoint joinPoint, WeylandWatchingYou weylandWatchingYou) throws Throwable {
        if (!enabled) {
            return joinPoint.proceed();
        }
        String auditId = UUID.randomUUID().toString();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String fullMethodName = String.format("%s.%s", className, methodName);

        logAuditEntry(auditId, "START", fullMethodName, null, null, null, weylandWatchingYou.value());

        try {
            Object result = joinPoint.proceed();
            logAuditEntry(auditId, "SUCCESS", fullMethodName, joinPoint.getArgs(), result, null, weylandWatchingYou.value());
            return result;
        } catch (Throwable throwable) {
            logAuditEntry(auditId, "ERROR", fullMethodName, joinPoint.getArgs(), null, throwable.getMessage(), weylandWatchingYou.value());
            throw throwable;
        }
    }


    private void logAuditEntry(String auditId, String status, String methodName,
                               Object[] args, Object result, String error, String description) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("auditId", auditId);
            auditData.put("timestamp", Instant.now().toString());
            auditData.put("status", status);
            auditData.put("method", methodName);
            auditData.put("description", description);

            if (args != null && args.length > 0) {
                auditData.put("arguments", args);
            }
            if (result != null) {
                auditData.put("result", result);
            }
            if (error != null) {
                auditData.put("error", error);
            }

            String auditMessage = objectMapper.writeValueAsString(auditData);

            if (mode == AuditMode.KAFKA && kafkaTemplate != null) {
                kafkaTemplate.send(kafkaTopic, auditMessage)
                        .whenComplete((sendResult, ex) -> {
                            if (ex == null) {
                                log.trace("Сообщение аудита отправлено: {}", auditId);
                            } else {
                                log.error("Ошибка при отправке сообщения аудита: {}", auditId, ex);
                            }
                        });
            } else if (mode == AuditMode.CONSOLE) {
                log.info("[AUDIT] {}", auditMessage);
            }
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации данных аудита: {}", e.getMessage(), e);
        }
    }
}


