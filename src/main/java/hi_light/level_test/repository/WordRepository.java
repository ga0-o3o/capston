package hi_light.level_test.repository;

import hi_light.level_test.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByLevel(String level); // 레벨별 조회
}