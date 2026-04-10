package bachtx.myapp.sso_service.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = authentication.getName();
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
