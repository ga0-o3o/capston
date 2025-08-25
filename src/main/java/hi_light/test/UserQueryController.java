package hi_light.test;

import hi_light.naver_login.entity.User;
import hi_light.naver_login.repository.UserRepository;
import hi_light.test.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hi_light/user")
@RequiredArgsConstructor
public class UserQueryController {

    private final UserRepository userRepository;

    @GetMapping("/getuser")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<UserDto> result = users.stream().map(UserDto::from).toList();
        System.out.println(users.getFirst().getName()+"출력 완료");
        return ResponseEntity.ok(result);
    }

    // UserQueryController.java
    @PostMapping("/add")
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto dto) {
        User user = User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .nickname(dto.getNickname())   // ← nickname 필드 추가해야 함
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(UserDto.from(user));
    }

}
