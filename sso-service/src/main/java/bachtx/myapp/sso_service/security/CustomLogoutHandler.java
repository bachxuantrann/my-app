package bachtx.myapp.sso_service.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            return;
        }
        String username = authentication.getName();
        log.info("Thực thi Custom Logout Handler cho user: {}", username);

        // Giả lập logic: Trích xuất Access Token Header hoặc đọc JTI từ Context
        // Code thực tế nếu dùng Opaque Token / JWT introspection có thể trích xuất "Authorization" header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            // Push token vào Redis Blacklist với TTL tương đương thời gian hết hạn của Token (Giả sử 1 giờ)
            String blacklistKey = "sso:blacklist:" + token;
            redisTemplate.opsForValue().set(blacklistKey, "revoked", 1, TimeUnit.HOURS);
            
            log.info("Token đã được đưa vào Redis Blacklist thành công: {}", blacklistKey);
        }

        // Tùy theo thiết kế, ta có thể gửi Event gọi Global Logout tới các Client ở đây
    }
}
