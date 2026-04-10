package bachtx.myapp.sso_service.repository;

import bachtx.myapp.sso_service.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, String> {
}
