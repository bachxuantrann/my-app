package bachtx.myapp.sso_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép truy cập tự do vào các trang public và static resources
                        .requestMatchers("/login", "/register", "/forgot-password", "/assets/**", "/css/**", "/js/**").permitAll()
                        // Tất cả các request khác đều bắt buộc phải đăng nhập
                        .anyRequest().authenticated()
                )
                // Cấu hình form login tùy chỉnh của chúng ta
                .formLogin(form -> form
                        .loginPage("/login") // Đường dẫn đến Controller trả về file HTML
                        .loginProcessingUrl("/login-process") // Đường dẫn Spring Security dùng để bắt action submit form
                        .permitAll()
                )
                // Cấu hình logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
