package hi_light.bean;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 💡 1. 토큰 검사를 건너뛸 경로 지정 (로그인, 회원가입 등)
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/user/save") ||
                requestURI.startsWith("/api/user/signup") ||
                requestURI.startsWith("/api/user/login") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-ui")) {

            filterChain.doFilter(request, response);
            return; // 이 경로는 바로 다음 필터로 넘기고 종료
        }

        // 💡 2. 이 아래부터는 토큰이 필요한 요청만 처리
        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
            Authentication authentication = getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Authentication getAuthentication(String token) {
        // 토큰에서 클레임(claims) 추출
        Claims claims = jwtProvider.getClaims(token);

        // 클레임에서 사용자 이름(username) 추출
        String username = claims.getSubject();

        // UserDetails 객체 생성 (사용자 정보)
        // 여기서는 간단하게 사용자 이름만 사용합니다.
        UserDetails userDetails = new User(username, "", Collections.emptyList());

        // Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }
}