package bachtx.myapp.sso_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_social_connections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSocialConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "provider")
    private String provider;
    @Column(name = "provider_account_id")
    private String providerAccountId;
    @Column(name = "email_verified")
    private Boolean emailVerified;
}
