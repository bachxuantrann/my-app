package bachtx.myapp.sso_service.repository;

import bachtx.myapp.sso_service.entity.Oauth2AuthorizationConsent;
import bachtx.myapp.sso_service.entity.Oauth2AuthorizationConsentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Oauth2AuthorizationConsentRepository extends JpaRepository<Oauth2AuthorizationConsent, Oauth2AuthorizationConsentId> {
    Optional<Oauth2AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
}
