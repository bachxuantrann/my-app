package bachtx.myapp.sso_service.repository;

import bachtx.myapp.sso_service.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Modifying
    @Query("DELETE FROM AuditLog a WHERE a.createdAt < :threshold")
    void deleteByCreatedAtBefore(@Param("threshold") LocalDateTime threshold);
}
