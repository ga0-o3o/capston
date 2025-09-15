package hi_light.personal_words.repository;

import hi_light.personal_words.entity.PersonalWords;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PersonalWordsRepository extends JpaRepository<PersonalWords, Long> {
    // 특정 user_id에 해당하는 모든 단어를 조회하는 메서드
    List<PersonalWords> findByUserId(String userId);

    // 게임 참가자들의 ID 목록을 받아 단어를 조회하는 메서드
    List<PersonalWords> findByUserIdIn(List<String> userIds);
}
