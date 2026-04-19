package bachtx.myapp.sso_service.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisIntrospectionFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Bug fix: getRequestURI() trả về "/sso-service/oauth2/introspect" (bao gồm context-path)
        // Phải dùng getServletPath() để lấy "/oauth2/introspect" không có context-path
        if ("/oauth2/introspect".equals(request.getServletPath()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String token = request.getParameter("token");

            if (token != null) {
                // ===== Kiểm tra 1: Token-level blacklist (cho API-based logout) =====
                String blacklistKey = "sso:blacklist:" + token;

                if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
                    log.warn("Introspection chối từ Token bị blacklist trực tiếp. Token: {}...", token.substring(0, Math.min(20, token.length())));
                    rejectToken(response);
                    return;
                }

                // ===== Kiểm tra 2: User-level revocation (cho Browser-based logout) =====
                // Parse JWT để lấy subject (username) và issued-at time (iat)
                try {
                    SignedJWT signedJWT = SignedJWT.parse(token);
                    JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
                    String subject = claims.getSubject();
                    Date issuedAt = claims.getIssueTime();

                    if (subject != null && issuedAt != null) {
                        String userRevokedKey = "sso:user-revoked:" + subject;
                        Object revokedTimestampObj = redisTemplate.opsForValue().get(userRevokedKey);

                        if (revokedTimestampObj != null) {
                            long revokedTimestamp;
                            if (revokedTimestampObj instanceof Number) {
                                revokedTimestamp = ((Number) revokedTimestampObj).longValue();
                            } else {
                                revokedTimestamp = Long.parseLong(revokedTimestampObj.toString());
                            }

                            long tokenIssuedAtEpoch = issuedAt.getTime() / 1000; // Convert ms → seconds

                            if (tokenIssuedAtEpoch <= revokedTimestamp) {
                                log.warn("Introspection chối từ Token của user '{}' — token được cấp tại epoch={} nhưng user đã logout tại epoch={}",
                                        subject, tokenIssuedAtEpoch, revokedTimestamp);
                                rejectToken(response);
                                return;
                            }
                        }
                    }
                } catch (ParseException e) {
                    // Token không phải JWT hợp lệ → để Spring Authorization Server xử lý tiếp
                    log.debug("Không thể parse JWT cho user-level revocation check: {}", e.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Trả response JSON {"active": false} cho token bị thu hồi
     */
    private void rejectToken(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"active\": false}");
    }
}
