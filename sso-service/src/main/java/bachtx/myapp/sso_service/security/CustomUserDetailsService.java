package bachtx.myapp.sso_service.security;

import bachtx.myapp.sso_service.constant.UserStatusEnum;
import bachtx.myapp.sso_service.entity.Role;
import bachtx.myapp.sso_service.entity.User;
import bachtx.myapp.sso_service.repository.RoleRepository;
import bachtx.myapp.sso_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username)
        );
        // Xử lý logic Brute-force (Khóa tài khoản tạm thời)
        boolean isAccountNonLocked = true;
        if (user.getLockedUntil() != null) {
            if (user.getLockedUntil().isAfter(LocalDateTime.now())) {
                isAccountNonLocked = false;
            } else {
                // Đã hết hạn phạt, reset lại data
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                userRepository.save(user);
            }
        }
        // Lưu ý: Fix bug so sánh Value ("Đã kích hoạt") thay vì Name ("ACTIVE")
        boolean isEnabled = UserStatusEnum.ACTIVE.name().equalsIgnoreCase(user.getStatus()) ||
                            UserStatusEnum.ACTIVE.getValue().equalsIgnoreCase(user.getStatus());
        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new RuntimeException("Lỗi dữ liệu: User không có Role hợp lệ"));
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getCode());
        return new CustomUserDetails(
                user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getEmailVerified() != null && user.getEmailVerified(),
                Collections.singletonList(authority), isAccountNonLocked, isEnabled
        );
    }
}
