//package hi_light.naver_login.controller;
//
//
//import hi_light.naver_login.entity.User;
//import hi_light.naver_login.repository.UserRepository;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.util.Date;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/naver")
//@RequiredArgsConstructor
//public class NaverLoginController {
//
//    private final UserRepository userRepository;
//
//    @Value("${naver.client-id}") private String clientId;
//    @Value("${naver.client-secret}") private String clientSecret;
//
//    // 기존 redirect-url 대신 아래 두 개 사용
//    @Value("${naver.redirect-url-app}") private String redirectUrlApp;
//    @Value("${naver.redirect-url-web-default}") private String redirectUrlWebDefault;
//
//    // 화이트리스트(없으면 빈 리스트)
//    @Value("#{'${app.allowed-web-redirects:}'.split(',')}")
//    private java.util.List<String> allowedWebRedirects;
//
//    @Value("${jwt.secret}") private String jwtSecret;
//
//    @GetMapping("/callback")
//    public ResponseEntity<?> naverCallback(@RequestParam String code,
//                                           @RequestParam String state) {
//        try {
//            // 1) 액세스 토큰 받기
//            RestTemplate restTemplate = new RestTemplate();
//            URI tokenUri = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
//                    .queryParam("grant_type", "authorization_code")
//                    .queryParam("client_id", clientId)
//                    .queryParam("client_secret", clientSecret)
//                    .queryParam("code", code)
//                    .queryParam("state", state)
//                    .build(true).toUri();
//
//            ResponseEntity<Map> tokenResp = restTemplate.getForEntity(tokenUri, Map.class);
//            if (!tokenResp.getStatusCode().is2xxSuccessful() || tokenResp.getBody() == null) {
//                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("토큰 수신 실패");
//            }
//            String accessToken = (String) tokenResp.getBody().get("access_token");
//
//            // 2) 프로필 받기
//            HttpHeaders h = new HttpHeaders();
//            h.set("Authorization", "Bearer " + accessToken);
//            ResponseEntity<Map> profileResp = restTemplate.exchange(
//                    "https://openapi.naver.com/v1/nid/me",
//                    HttpMethod.GET,
//                    new HttpEntity<>(h),
//                    Map.class
//            );
//            if (!profileResp.getStatusCode().is2xxSuccessful() || profileResp.getBody() == null) {
//                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("프로필 수신 실패");
//            }
//
//            Map<String, Object> profile = (Map<String, Object>) profileResp.getBody().get("response");
//            String userId = String.valueOf(profile.get("id"));             // 네이버 고유 ID
//            String name    = String.valueOf(profile.getOrDefault("name", "사용자"));
//
//            // 3) 사용자 DB upsert
//            String uid = "NAVER" + userId;
//            if (!userRepository.existsById(uid)) {
//                userRepository.save(User.builder().id(uid).name(name).build());
//            }
//
//            // 4) JWT 생성 (비밀키 최소 32바이트 확인!)
//            String jwtToken = Jwts.builder()
//                    .claim("name", name)              // <= 여기서 name 정의됨
//                    .setSubject(uid)                  // <= 여기서 uid 정의됨
//                    .setIssuedAt(new Date())
//                    .setExpiration(new Date(System.currentTimeMillis() + 3600000))
//                    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
//                    .compact();
//
//            // 5) state 우선, 실패 시 yml 폴백으로 리다이렉트
//            String target = "app";
//            String returnUrl = null;
//            if (state != null && state.startsWith("web|")) {
//                target = "web";
//                returnUrl = state.substring("web|".length());
//            } else if ("app".equalsIgnoreCase(state)) {
//                target = "app";
//            }
//
//            String finalRedirect;
//            if ("web".equals(target) && isAllowed(returnUrl)) {
//                finalRedirect = UriComponentsBuilder.fromHttpUrl(returnUrl)
//                        .queryParam("token", jwtToken).build(true).toUriString();
//            } else if ("web".equals(target)) {
//                finalRedirect = UriComponentsBuilder.fromHttpUrl(redirectUrlWebDefault)
//                        .queryParam("token", jwtToken).build(true).toUriString();
//            } else {
//                finalRedirect = UriComponentsBuilder.fromUriString(redirectUrlApp)
//                        .queryParam("token", jwtToken).build(true).toUriString();
//            }
//
//            HttpHeaders redirectHeaders = new HttpHeaders();
//            redirectHeaders.setLocation(URI.create(finalRedirect));
//            return new ResponseEntity<>(redirectHeaders, HttpStatus.FOUND);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("네이버 로그인 실패: " + e.getMessage());
//        }
//    }
//
//
//    private boolean isAllowed(String url) {
//        if (url == null || url.isBlank()) return false;
//        // startsWith로 간단 검증(운영에선 더 엄격 권장)
//        return allowedWebRedirects.stream().anyMatch(url::startsWith);
//    }
//}
