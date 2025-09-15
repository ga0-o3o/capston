package hi_light.user.service;

import hi_light.user.entity.UserRank;
import hi_light.user.entity.User;
import hi_light.user.dto.UserResponseDto;
import hi_light.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // PasswordEncoder 임포트
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Transactional
    public User updateUserRank(String userId, String newRankString) {
        // 1. 사용자 ID로 기존 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 새로운 레벨(newRankString)과 기존 레벨(user.getUserRank())을 UserRank enum으로 변환
        try {
            UserRank currentRank = UserRank.valueOf(user.getUserRank());
            UserRank newRank = UserRank.valueOf(newRankString);

            // 3. 레벨 비교 로직 적용
            // 새로운 레벨이 기존 레벨보다 높은 경우에만 업데이트
            if (newRank.getRankValue() > currentRank.getRankValue()) {
                user.setUserRank(newRankString); // 새로운 레벨로 업데이트
                return userRepository.save(user); // DB에 저장
            } else {
                // 새로운 레벨이 기존 레벨보다 낮거나 같으면 업데이트하지 않고 기존 객체 반환
                return user;
            }
        } catch (IllegalArgumentException e) {
            // enum 변환에 실패하면 유효하지 않은 레벨 값으로 예외 처리
            throw new IllegalArgumentException("유효하지 않은 레벨 값입니다.", e);
        }
    }

    // 비밀번호를 제외한 전체 사용자 정보 조회 메서드
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsersWithoutPw() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDto::new) // User 엔티티를 UserResponseDto로 변환
                .collect(Collectors.toList());
    }

    // ⭐ 사용자 ID로 랭크를 조회하는 메서드 추가
    @Transactional(readOnly = true)
    public Optional<String> getUserRankById(String id) {
        return userRepository.findById(id).map(User::getUserRank);
    }
}
