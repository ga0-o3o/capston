package hi_light.user.controller;

import hi_light.bean.JwtProvider;
import hi_light.user.entity.User;
import hi_light.user.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor

public class KakaoController {
    private final KakaoService userService;
    private final JwtProvider jwtProvider; // JwtProvider 주입

    // 카카오 로그인 후 DB 저장
    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@RequestBody Map<String, String> userInfo) {
        System.out.print("!!!!!!!!!! 받아오려고 접근");
        String id = "KAKAO" + userInfo.get("id"); // 프론트에서 받은 카카오 ID
        String name = userInfo.get("name");
        System.out.print("!!!!!!!!!! 받아와짐");

        // DB 저장
        User user = userService.saveOrUpdateUser(id, name);
        System.out.print("!!!!!!!!!! DB에 저장함");

        // JWT 토큰 생성
        String token = jwtProvider.createToken(user.getId()); // 사용자 ID를 기반으로 토큰 생성

        // 저장된 사용자 정보 반환
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "nickname", user.getNickname(),
                "userRank", user.getUserRank(),
                "token", token // 생성된 토큰을 응답에 추가
        ));
    }
}
