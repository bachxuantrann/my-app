package bachtx.myapp.sso_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oauth2_authorization_consent")
@IdClass(Oauth2AuthorizationConsentId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Oauth2AuthorizationConsent {
    @Id
    @Column(name = "registered_client_id", nullable = false, length = 100)
    private String registeredClientId;

    @Id
    @Column(name = "principal_name", nullable = false, length = 200)
    private String principalName;

    @Column(name = "authorities", nullable = false, length = 1000)
    private String authorities;
}
