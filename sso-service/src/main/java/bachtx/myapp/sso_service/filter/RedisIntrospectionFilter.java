package bachtx.myapp.sso_service.filter;

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
                String blacklistKey = "sso:blacklist:" + token;

                if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
                    log.warn("Introspection chối từ Token hỏng/thu hồi. Token: {}", token);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"active\": false}");
                    return;
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
