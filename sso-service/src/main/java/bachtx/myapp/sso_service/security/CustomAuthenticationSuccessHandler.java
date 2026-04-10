package bachtx.myapp.sso_service.security;

import bachtx.myapp.sso_service.entity.User;
import bachtx.myapp.sso_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = authentication.getName();
        
        // Cập nhật lastLoginAt và fail attempts
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLastLoginAt(LocalDateTime.now());
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ADMIN".equals(a.getAuthority()));

        if (isAdmin) {
            log.info("Admin '{}' logged in → redirect to /admin/dashboard", username);
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            log.info("User '{}' logged in → redirect to /profile", username);
            response.sendRedirect(request.getContextPath() + "/profile");
        }
    }
}
