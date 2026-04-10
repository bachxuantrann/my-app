package bachtx.myapp.sso_service.repository;

import bachtx.myapp.sso_service.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
