package bachtx.myapp.sso_service.config;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import bachtx.myapp.sso_service.security.CustomAuthenticationSuccessHandler;
import bachtx.myapp.sso_service.security.CustomLogoutHandler;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomLogoutHandler customLogoutHandler;
    private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Dùng CookieCsrfTokenRepository để CSRF token lưu trong cookie thay vì session.
        // Lý do: Nếu CSRF token lưu trong session, trang /login sẽ luôn tạo session mới
        // (ngay cả sau logout) → sinh ra SSO_SESSION cookie rác.
        // Với CookieCsrfTokenRepository, CSRF token nằm trong cookie XSRF-TOKEN riêng,
        // trang /login không cần tạo HttpSession → không sinh SSO_SESSION sau logout.
        CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
        csrfTokenRepository.setCookiePath("/sso-service");
        csrfTokenRepository.setCookieHttpOnly(true);

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                        .csrfTokenRepository(csrfTokenRepository)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/register", "/forgot-password",
                                "/assets/**", "/css/**", "/js/**", "/error").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/profile").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login-process")
                        // Redirect theo role: ADMIN → dashboard, USER → profile
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(customLogoutHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        // Dùng custom LogoutSuccessHandler thay vì deleteCookies() + logoutSuccessUrl()
                        // Lý do: deleteCookies() tạo cookie xoá với path="/" nhưng SSO_SESSION có path="/sso-service"
                        // → Browser không xoá cookie vì path không khớp → cookie bị refresh value thay vì xoá
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .permitAll()
                );

        return http.build();
    }

    /**
     * Custom LogoutSuccessHandler xoá cookie với ĐÚNG path="/sso-service"
     * để browser thực sự xoá cookie thay vì giữ lại.
     */
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            // Xoá SSO_SESSION cookie với đúng path (phải khớp với SessionConfig.cookiePath)
            Cookie ssoCookie = new Cookie("SSO_SESSION", null);
            ssoCookie.setPath("/sso-service");
            ssoCookie.setMaxAge(0);
            ssoCookie.setHttpOnly(true);
            response.addCookie(ssoCookie);

            // Xoá JSESSIONID cookie (phòng trường hợp Tomcat tạo ra)
            Cookie jsessionCookie = new Cookie("JSESSIONID", null);
            jsessionCookie.setPath("/sso-service");
            jsessionCookie.setMaxAge(0);
            jsessionCookie.setHttpOnly(true);
            response.addCookie(jsessionCookie);

            // Xoá XSRF-TOKEN cookie (dọn sạch sau logout)
            Cookie xsrfCookie = new Cookie("XSRF-TOKEN", null);
            xsrfCookie.setPath("/sso-service");
            xsrfCookie.setMaxAge(0);
            xsrfCookie.setHttpOnly(true);
            response.addCookie(xsrfCookie);

            // Redirect tới login page
            response.sendRedirect(request.getContextPath() + "/login?logout");
        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
