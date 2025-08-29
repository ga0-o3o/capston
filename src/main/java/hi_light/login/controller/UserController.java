package hi_light.login.controller;

import hi_light.login.entity.User;
import hi_light.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;

    // 카카오 로그인 후 DB 저장
    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@RequestBody Map<String, String> userInfo) {
        String id = "KAKAO" + userInfo.get("id"); // 프론트에서 받은 카카오 ID
        String name = userInfo.get("name");

        // DB 저장
        User user = userService.saveOrUpdateUser(id, name);

        // 저장된 사용자 정보 반환
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "nickname", user.getNickname()
        ));
    }
}
