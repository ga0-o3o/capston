package hi_light.naver_login.repository;


import hi_light.naver_login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
