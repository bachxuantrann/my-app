package bachtx.myapp.sso_service.event;

import bachtx.myapp.sso_service.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        publishEvent("LOGIN_SUCCESS", username, "Bên thứ 3 / Web SSO Đăng nhập Thành Công");
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        publishEvent("LOGIN_FAILED", username, "Sai thông tin đăng nhập");
    }

    public void publishEvent(String eventType, String username, String details) {
        Map<String, Object> auditMessage = new HashMap<>();
        auditMessage.put("eventType", eventType);
        auditMessage.put("username", username);
        auditMessage.put("details", details);
        auditMessage.put("timestamp", LocalDateTime.now().toString());

        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String ipAddress = request.getHeader("X-Forwarded-For");
                if (ipAddress == null || ipAddress.isEmpty()) {
                    ipAddress = request.getRemoteAddr();
                }
                auditMessage.put("ipAddress", ipAddress);
                auditMessage.put("userAgent", request.getHeader("User-Agent"));
            }
        } catch (Exception ignored) {
        }

        try {
            kafkaTemplate.send(KafkaConfig.AUDIT_TOPIC, username, auditMessage);
            log.info("Khởi tạo Audit Event thành công tới Kafka Topic");
        } catch (Exception e) {
            log.error("Lỗi khi gửi Audit Event tới Kafka", e);
        }
    }
}
