package bachtx.myapp.sso_service.config;

import bachtx.myapp.sso_service.security.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Set;

@Configuration
public class TokenCustomizerConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return (context) -> {
            // Chỉ nhét data vào access_token hoặc id_token
            if ("access_token".equals(context.getTokenType().getValue()) ||
                    "id_token".equals(context.getTokenType().getValue())) {

                Authentication principal = context.getPrincipal();
                Set<String> authorizedScopes = context.getAuthorizedScopes();

                // Trích xuất UserDetails mà chúng ta đã setup ở bước 2 & 3
                if (principal.getPrincipal() instanceof CustomUserDetails userDetails) {

                    // Cung cấp id, username cho profile
                    if (authorizedScopes != null && authorizedScopes.contains("profile")) {
                        context.getClaims().claim("userId", userDetails.getId());
                        context.getClaims().claim("username", userDetails.getUsername());
                    }

                    // Cung cấp email nếu có yêu cầu
                    if (authorizedScopes != null && authorizedScopes.contains("email") && userDetails.getEmail() != null) {
                        context.getClaims().claim("email", userDetails.getEmail());
                        context.getClaims().claim("email_verified", userDetails.isEmailVerified());
                    }

                    // Cung cấp roles nếu muốn đẩy logic role (có thể đặt custom scope gọi là roles)
                    if (authorizedScopes != null && authorizedScopes.contains("roles")) {
                         context.getClaims().claim("roles", userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority).toList());
                    }
                }
            }
        };
    }
}
