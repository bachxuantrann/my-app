package bachtx.myapp.sso_service.repository;

import bachtx.myapp.sso_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
