package hi_light.user.service;

import hi_light.user.entity.User;
import hi_light.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class KakaoService {
    private final UserRepository userRepository;

    // DB 저장 (id가 없으면 새로 저장, 있으면 기존 사용자 반환)
    public User saveOrUpdateUser(String id, String name) {
        if (!userRepository.existsById(id)) {
            return userRepository.save(User.builder()
                    .id(id)
                    .name(name)
                    .nickname(name) // nickname 초기값 = name
                    .userRank("A1")
                    .build());
        }
        return userRepository.findById(id).get();
    }
}

