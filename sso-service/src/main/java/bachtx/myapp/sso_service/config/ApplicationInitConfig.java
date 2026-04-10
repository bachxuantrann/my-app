package bachtx.myapp.sso_service.config;

import bachtx.myapp.sso_service.constant.UserRoleEnum;
import bachtx.myapp.sso_service.constant.UserStatusEnum;
import bachtx.myapp.sso_service.entity.Role;
import bachtx.myapp.sso_service.entity.User;
import bachtx.myapp.sso_service.exception.AppException;
import bachtx.myapp.sso_service.exception.ErrorCode;
import bachtx.myapp.sso_service.repository.RoleRepository;
import bachtx.myapp.sso_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void initData() {
        initRole(UserRoleEnum.ADMIN);
        initRole(UserRoleEnum.USER);

        initAdmin();
    }

    private void initRole(UserRoleEnum roleEnum) {
        roleRepository.findByCode(roleEnum.getCode())
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .code(roleEnum.getCode())
                            .description(roleEnum.getDescription())
                            .build();
                    log.info("Init role {} success to database", roleEnum.getCode());
                    return roleRepository.save(role);
                });
    }

    private void initAdmin() {
        userRepository.findByUsername(ADMIN_USERNAME)
                .orElseGet(() -> {

                    Role adminRole = roleRepository
                            .findByCode(UserRoleEnum.ADMIN.getCode())
                            .orElseThrow(
                                    () -> new AppException(ErrorCode.INIT_ROLE_ERROR)
                            );

                    User admin = User.builder()
                            .username(ADMIN_USERNAME)
                            .password(passwordEncoder.encode(ADMIN_PASSWORD))
                            .status(UserStatusEnum.ACTIVE.getValue())
                            .roleId(adminRole.getId())
                            .build();
                    log.info("Init admin with username: {} success to database", ADMIN_USERNAME);
                    return userRepository.save(admin);
                });
    }
}
