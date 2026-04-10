package bachtx.myapp.sso_service.repository;

import bachtx.myapp.sso_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByCode(String code);
}
