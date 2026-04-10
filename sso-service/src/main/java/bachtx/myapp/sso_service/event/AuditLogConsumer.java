package bachtx.myapp.sso_service.event;

import bachtx.myapp.sso_service.config.KafkaConfig;
import bachtx.myapp.sso_service.entity.AuditLog;
import bachtx.myapp.sso_service.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogConsumer {

    private final AuditLogRepository auditLogRepository;

    @KafkaListener(topics = KafkaConfig.AUDIT_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAuditLog(Map<String, Object> message) {
        try {
            log.info("Received Audit Event: {}", message);
            String eventType = (String) message.get("eventType");
            String username = (String) message.get("username");
            String details = (String) message.get("details");
            
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(username);
            auditLog.setAction(eventType);
            auditLog.setDetails(details);
            auditLog.setCreatedAt(LocalDateTime.now());
            auditLog.setCreatedBy("system");

            auditLogRepository.save(auditLog);
            log.info("Successfully persisted audit log for user: {}", username);
        } catch (Exception e) {
            log.error("Failed to process audit log message", e);
        }
    }
}
