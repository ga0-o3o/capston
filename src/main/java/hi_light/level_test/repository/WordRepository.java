package hi_light.level_test.repository;

import hi_light.level_test.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // @Param 임포트

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByLevel(String level); // 레벨별 조회

    @Query(value = "SELECT * FROM level_test_words WHERE level = :level ORDER BY RAND() LIMIT 30", nativeQuery = true)
    List<Word> findRandomWordsByLevel(@Param("level") String level);
}