package bachtx.myapp.sso_service.config;

import bachtx.myapp.sso_service.utils.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // Gọi hàm lấy Username vừa tạo
            String currentUsername = SecurityUtils.getCurrentUsername();

            if (currentUsername != null) {
                // Trả về username (ví dụ: "admin", "bachtx")
                return Optional.of(currentUsername);
            } else {
                // Trả về SYSTEM nếu thao tác được thực hiện bởi hệ thống (chưa đăng nhập)
                return Optional.of("SYSTEM");
            }
        };
    }
}
