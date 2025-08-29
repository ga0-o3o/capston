package hi_light.login.service;

import hi_light.login.entity.User;
import hi_light.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UserService {
    private final UserRepository userRepository;

    // DB 저장 (id가 없으면 새로 저장, 있으면 기존 사용자 반환)
    public User saveOrUpdateUser(String id, String name) {
        if (!userRepository.existsById(id)) {
            return userRepository.save(User.builder()
                    .id(id)
                    .name(name)
                    .nickname(name) // nickname 초기값 = name
                    .build());
        }
        return userRepository.findById(id).get();
    }
}

