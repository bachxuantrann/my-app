package bachtx.myapp.sso_service.task;

import bachtx.myapp.sso_service.entity.SystemSetting;
import bachtx.myapp.sso_service.repository.AuditLogRepository;
import bachtx.myapp.sso_service.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogCleanupTask {

    private final AuditLogRepository auditLogRepository;
    private final SystemSettingRepository systemSettingRepository;

    // Chạy lúc 2 giờ sáng mỗi ngày
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanUpOldLogs() {
        log.info("Starting scheduled task: Audit Log Cleanup");
        
        int retentionDays = 30; // default
        try {
            SystemSetting setting = systemSettingRepository.findById("audit_log_retention_days").orElse(null);
            if (setting != null && setting.getValue() != null) {
                retentionDays = Integer.parseInt(setting.getValue());
            }
        } catch (Exception e) {
            log.warn("Invalid retention days configured, falling back to 30 days");
        }

        LocalDateTime threshold = LocalDateTime.now().minus(retentionDays, ChronoUnit.DAYS);
        log.info("Deleting logs older than {} days (Threshold: {})", retentionDays, threshold);

        // Delete logs using custom method or direct query
        // Implement custom query in AuditLogRepository
        auditLogRepository.deleteByCreatedAtBefore(threshold);
        
        log.info("Finished: Audit Log Cleanup");
    }
}
