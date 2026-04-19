package bachtx.myapp.sso_service.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Xử lý logout cho cả 2 luồng:
     * 1. Browser form logout (POST /logout) → không có Bearer token, chỉ có session
     * 2. API logout (nếu có Authorization header) → blacklist token trực tiếp
     *
     * Chiến lược: Lưu mốc thời gian logout của user vào Redis.
     * RedisIntrospectionFilter sẽ kiểm tra: nếu token được cấp TRƯỚC thời điểm logout
     * → token bị coi là đã thu hồi (revoked).
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            log.warn("Logout được gọi nhưng Authentication là null (user chưa đăng nhập hoặc session đã hết hạn)");
            return;
        }
        String username = authentication.getName();
        log.info("Thực thi Custom Logout Handler cho user: {}", username);

        // ===== Chiến lược 1: Blacklist token trực tiếp (cho API-based logout) =====
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Push token vào Redis Blacklist với TTL tương đương thời gian hết hạn của Token (1 giờ)
            String blacklistKey = "sso:blacklist:" + token;
            redisTemplate.opsForValue().set(blacklistKey, "revoked", 1, TimeUnit.HOURS);

            log.info("Token đã được đưa vào Redis Blacklist thành công: {}", blacklistKey);
        }

        // ===== Chiến lược 2: User-level revocation (cho Browser-based logout) =====
        // Lưu mốc thời gian logout → mọi token được cấp TRƯỚC thời điểm này đều bị coi là revoked
        String userRevokedKey = "sso:user-revoked:" + username;
        long logoutTimestamp = Instant.now().getEpochSecond();
        redisTemplate.opsForValue().set(userRevokedKey, logoutTimestamp, 1, TimeUnit.HOURS);

        log.info("Đã đánh dấu thu hồi user-level cho '{}' tại epoch={} (key={})", username, logoutTimestamp, userRevokedKey);

        // Tùy theo thiết kế, ta có thể gửi Event gọi Global Logout tới các Client ở đây
    }
}
