package hi_light.user.service;

import hi_light.user.entity.User;
import hi_light.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // PasswordEncoder 임포트
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    // 회원가입
    public User signup(String id, String pw, String name) {
        if (userRepository.existsById(id)) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
        }

        User user = User.builder()
                .id(id)
                .pw(passwordEncoder.encode(pw))
                .name(name)
                .nickname(name)
                .userRank(null)
                .build();

        return userRepository.save(user);
    }

    // 로그인
    @Transactional(readOnly = true)
    public Optional<User> login(String id, String pw) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // 입력받은 비밀번호(평문)와 DB에 저장된 암호화된 비밀번호를 비교
            if (passwordEncoder.matches(pw, user.getPw())) {
                return Optional.of(user);
            }
        }
        return Optional.empty(); // 로그인 실패 시 Optional.empty() 반환
    }

    // 닉네임 변경
    public User updateNickname(String id, String newNickname) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        user.setNickname(newNickname);
        return userRepository.save(user);
    }

    // 레벨테스트 후 userRank 업데이트
    public User updateUserRank(String userId, String newRank) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setUserRank(newRank); // userRank 업데이트
        return userRepository.save(user);
    }
}
