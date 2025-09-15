package hi_light.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Component
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    private static final String SECRET_KEY = "YourSecretKeyForJWT"; // 실제 시크릿 키로 변경 필요

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // HTTP 요청 헤더에서 JWT 토큰을 가져옴
        String token = extractJwtToken(request);
        if (token != null) {
            try {
                // 토큰 유효성 검사 및 사용자 ID 추출
                Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
                String userId = claims.getSubject();
                attributes.put("userId", userId);
                log.info("Handshake successful for user: {}", userId);
                return true;
            } catch (Exception e) {
                log.error("JWT token validation failed: {}", e.getMessage());
                return false; // 토큰이 유효하지 않으면 연결 거부
            }
        }
        log.warn("Handshake failed: No JWT token found in request.");
        return false; // 토큰이 없으면 연결 거부
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Handshake 이후 로직 (필요시 구현)
    }

    private String extractJwtToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}