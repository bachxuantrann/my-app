package bachtx.myapp.sso_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username", nullable = false, length = 20)
    private String username;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "email_verified")
    private Boolean emailVerified;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;
    @Column(name = "failed_login_attemps")
    private Integer failedLoginAttempts;
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    @Column(name = "role_id")
    private Long roleId;
}
