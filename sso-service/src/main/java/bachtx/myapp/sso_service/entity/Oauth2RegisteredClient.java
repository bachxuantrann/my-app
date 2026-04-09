package bachtx.myapp.sso_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "oauth2_registered_client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oauth2RegisteredClient extends BaseAuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "client_id", nullable = false)
    private String clientId;
    @Column(name = "client_id_issued_at", nullable = false)
    private Instant clientIdIssuedAt;
    @Column(name = "client_secret", nullable = false)
    private String clientSecret;
    @Column(name = "client_secret_expires_at", nullable = false)
    private Instant clientSecretExpiresAt;
    @Column(name = "client_name", nullable = false)
    private String clientName;
    @Column(name = "client_authentication_methods", nullable = false)
    private String clientAuthenticationMethods;
    @Column(name = "authorization_grant_types", nullable = false)
    private String authorizationGrantTypes;
    @Column(name = "redirect_uris", nullable = false, columnDefinition = "TEXT")
    private String redirectUris;
    @Column(name = "post_logout_redirect_uris", nullable = false, columnDefinition = "TEXT")
    private String postLogoutRedirectUris;
    @Column(name = "scopes", nullable = false)
    private String scopes;
    @Column(name = "client_settings", columnDefinition = "TEXT")
    private String clientSettings;
    @Column(name = "token_settings", columnDefinition = "TEXT")
    private String tokenSettings;
}
