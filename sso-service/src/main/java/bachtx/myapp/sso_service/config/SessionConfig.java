package bachtx.myapp.sso_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        
        // Cố định tên Cookie
        serializer.setCookieName("SSO_SESSION");
        
        // Cố định Path là /sso-service để trình duyệt nhận diện đúng khi xóa
        serializer.setCookiePath("/sso-service");
        
        // Tắt Base64 để ID trong Cookie khớp hoàn toàn với ID trong Redis (giúp bạn dễ debug)
        serializer.setUseBase64Encoding(false);
        
        return serializer;
    }
}
