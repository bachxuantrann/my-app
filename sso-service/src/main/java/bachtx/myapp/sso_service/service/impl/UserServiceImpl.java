package bachtx.myapp.sso_service.service.impl;

import bachtx.myapp.sso_service.constant.UserRoleEnum;
import bachtx.myapp.sso_service.constant.UserStatusEnum;
import bachtx.myapp.sso_service.dto.request.RegisterRequest;
import bachtx.myapp.sso_service.entity.Role;
import bachtx.myapp.sso_service.entity.User;
import bachtx.myapp.sso_service.exception.AppException;
import bachtx.myapp.sso_service.exception.ErrorCode;
import bachtx.myapp.sso_service.repository.RoleRepository;
import bachtx.myapp.sso_service.repository.UserRepository;
import bachtx.myapp.sso_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void registerUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role userRole = roleRepository.findByCode(UserRoleEnum.USER.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.INIT_ROLE_ERROR, "Không tìm thấy Role USER"));

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatusEnum.ACTIVE.name()) // Lưu "ACTIVE" để đồng nhất với logic check
                .roleId(userRole.getId())
                .emailVerified(false)
                .failedLoginAttempts(0)
                .build();

        userRepository.save(newUser);
        log.info("User registered successfully: {}", request.getUsername());
    }

    @Override
    public void processForgotPassword(String email) {
        // Find user by email (we need to assume email is unique, though entity allows null)
        // Here we just mock the process
        log.info("========== MOCK EMAIL SERVICE ==========");
        log.info("Yêu cầu reset mật khẩu cho email: {}", email);
        String resetToken = UUID.randomUUID().toString();
        log.info("Link khôi phục mật khẩu của bạn là: http://localhost:8080/reset-password?token={}", resetToken);
        log.info("========================================");
    }
}
