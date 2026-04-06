package bachtx.myapp.sso_service.repository;

import bachtx.myapp.sso_service.entity.UserSocialConnection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSocialConnectionRepository extends JpaRepository<UserSocialConnection, Long> {
}
