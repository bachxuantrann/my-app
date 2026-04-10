package bachtx.myapp.sso_service.repository;

import bachtx.myapp.sso_service.entity.Oauth2RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Oauth2RegisteredClientRepository extends JpaRepository<Oauth2RegisteredClient, Long> {
    Optional<Oauth2RegisteredClient> findByClientId(String clientId);
}
