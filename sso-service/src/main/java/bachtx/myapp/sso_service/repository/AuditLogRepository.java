package bachtx.myapp.sso_service.repository;

import bachtx.myapp.sso_service.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Modifying
    @Query("DELETE FROM AuditLog a WHERE a.createdAt < :threshold")
    void deleteByCreatedAtBefore(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:username IS NULL OR LOWER(a.username) LIKE LOWER(CONCAT('%', CAST(:username AS string), '%'))) AND " +
            "(CAST(:startDate AS timestamp) IS NULL OR a.createdAt >= :startDate) AND " +
            "(CAST(:endDate AS timestamp) IS NULL OR a.createdAt <= :endDate)")
    Page<AuditLog> findWithFilters(@Param("username") String username,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);
}
