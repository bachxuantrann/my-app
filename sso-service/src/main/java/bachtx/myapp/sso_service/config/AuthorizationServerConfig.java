package bachtx.myapp.sso_service.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Khởi tạo đối tượng Configurer chuẩn của Spring Authorization Server
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        http
                // Chỉ định Filter này chỉ hoạt động trên các endpoint của SSO
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                // Áp dụng các cấu hình mặc định và bật OpenID Connect 1.0 bằng cú pháp Lambda DSL mới
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .authorizationEndpoint(authorizationEndpoint ->
                                        authorizationEndpoint.consentPage("/oauth2/consent")
                                )
                                .oidc(oidc -> oidc
                                        .providerConfigurationEndpoint(Customizer.withDefaults())
                                        .logoutEndpoint(Customizer.withDefaults())
                                )
                )
                // Cấu hình điều hướng về trang đăng nhập nếu chưa xác thực
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                // Phải khớp với context-path trong application.yaml
                                new LoginUrlAuthenticationEntryPoint("/sso-service/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.build();
    }

    // Bean chứa các cấu hình chung của Authorization Server (như URL gốc của server)
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                // issuer phải khớp với context-path. Khi deploy thật thì đổi thành domain + context-path
                .issuer("http://localhost:8080/sso-service")
                .build();
    }

    // =========================================================
    // PHẦN CẤU HÌNH BỘ KHÓA RSA ĐỂ KÝ JWT
    // =========================================================

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/sso-jwt.jks");
            if (is == null) {
                throw new IllegalStateException("Không tìm thấy sso-jwt.jks trong thư mục resources");
            }
            java.security.KeyStore keyStore = java.security.KeyStore.getInstance("JKS");
            keyStore.load(is, "sso-secret".toCharArray());

            RSAKey rsaKey = RSAKey.load(keyStore, "sso-jwt", "sso-secret".toCharArray());
            JWKSet jwkSet = new JWKSet(rsaKey);
            return new ImmutableJWKSet<>(jwkSet);
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể load RSA KeyStore cho chữ ký số JWT", ex);
        }
    }
}