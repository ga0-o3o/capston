package hi_light.user.repository;

import hi_light.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);
    Optional<User> findByIdAndPw(String id, String pw); // 로그인용
}
