package hi_light.bean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    private final Key key;
    private final long tokenValidityInMilliseconds;

    public JwtProvider(@Value("${jwt.secret}") String secretKey,
                       @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    // 토큰 생성
    public String createToken(String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(username) // 토큰의 주체(사용자 이름)
                .setIssuedAt(now)     // 토큰 발행 시간
                .setExpiration(validity) // 토큰 만료 시간
                .signWith(key) // 알고리즘을 직접 명시하지 않고, Key에 포함된 정보 사용
                .compact();
    }

    // 토큰에서 클레임(사용자 정보) 추출
    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우에도 클레임을 반환하도록 처리 (로그아웃 유도 등)
            return e.getClaims();
        }
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // 잘못된 JWT 서명
            // 로그로 남기는 것이 좋습니다.
        } catch (ExpiredJwtException e) {
            // 만료된 JWT 토큰
            // 로그로 남기는 것이 좋습니다.
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰
            // 로그로 남기는 것이 좋습니다.
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 잘못됨
            // 로그로 남기는 것이 좋습니다.
        }
        return false;
    }
}
