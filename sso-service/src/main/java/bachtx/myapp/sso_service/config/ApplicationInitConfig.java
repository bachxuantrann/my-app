package bachtx.myapp.sso_service.config;

import bachtx.myapp.sso_service.constant.UserRoleEnum;
import bachtx.myapp.sso_service.constant.UserStatusEnum;
import bachtx.myapp.sso_service.entity.Role;
import bachtx.myapp.sso_service.entity.User;
import bachtx.myapp.sso_service.repository.RoleRepository;
import bachtx.myapp.sso_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ApplicationInitConfig {
    final PasswordEncoder passwordEncoder;
    final UserRepository userRepository;
    final RoleRepository roleRepository;

    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "123456";

    @PostConstruct
    public void initData() {
        // Init role
        Role adminRole = Role.builder()
                .id(UserRoleEnum.ADMIN.getId())
                .code(UserRoleEnum.ADMIN.getCode())
                .description(UserRoleEnum.ADMIN.getDescription())
                .build();
        Role userRole = Role.builder()
                .id(UserRoleEnum.USER.getId())
                .code(UserRoleEnum.USER.getCode())
                .description(UserRoleEnum.USER.getDescription())
                .build();
        roleRepository.save(adminRole);
        roleRepository.save(userRole);

        // Init admin account
        Optional<User> adminOpt = userRepository.findByUsername(ADMIN_USERNAME);
        if (adminOpt.isEmpty()) {
            Role adminRoleCurrent = roleRepository.findById(UserRoleEnum.ADMIN.getId()).orElseThrow();
            User admin = User.builder()
                    .username(ADMIN_USERNAME)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .status(UserStatusEnum.ACTIVE.getValue())
                    .roleId(adminRoleCurrent.getId())
                    .build();
            userRepository.save(admin);
            log.info("Created default admin user: {}", admin.getUsername());
        } else {
            log.info("Admin account already exists, skip creating admin account");
        }
    }
}
