package bachtx.myapp.sso_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Oauth2RegisteredClient extends JpaRepository<Oauth2RegisteredClient, Long> {
}
