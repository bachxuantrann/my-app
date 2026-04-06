package bachtx.myapp.sso_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogs extends JpaRepository<AuditLogs, Long> {
}
