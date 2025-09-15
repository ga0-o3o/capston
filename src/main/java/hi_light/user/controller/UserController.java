package hi_light.user.controller;

import hi_light.bean.JwtProvider; // JwtProvider 임포트
import hi_light.user.dto.UserDto;
import hi_light.user.entity.User;
import hi_light.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import hi_light.user.dto.UserResponseDto;
import java.util.List;

import java.util.Optional;
import java.util.Map;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider; // JwtProvider 주입


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto) {
        try {
            User user = userService.signup(userDto.getId(), userDto.getPw(), userDto.getName());

            // 회원가입 성공 시 JWT 토큰 생성
            String token = jwtProvider.createToken(user.getId());

            return ResponseEntity.ok(Map.of(
                    "user", user,
                    "token", token
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        Optional<User> optionalUser = userService.login(userDto.getId(), userDto.getPw());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // 로그인 성공 시 JWT 토큰 생성
            String token = jwtProvider.createToken(user.getId());

            // 사용자 정보와 함께 토큰을 응답
            return ResponseEntity.ok(Map.of(
                    "message", "로그인 성공",
                    "user", user,
                    "token", token
            ));
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "로그인 실패"));
        }
    }

    // 닉네임 변경
    @PutMapping("/nickname")
    public User updateNickname(@Valid @RequestBody UserDto userDto) {
        return userService.updateNickname(userDto.getId(), userDto.getNickname());
    }

    // 레벨테스트 후 userRank 업데이트
    @PostMapping("/update-rank")
    public User updateUserRank(@RequestBody UserDto userDto) {
        return userService.updateUserRank(userDto.getId(), userDto.getUserRank());
    }

    // 비밀번호를 제외한 전체 사용자 정보 조회
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsersWithoutPw();
        return ResponseEntity.ok(users);
    }
}
