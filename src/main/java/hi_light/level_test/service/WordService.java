package hi_light.level_test.service;

import hi_light.level_test.entity.Word;
import hi_light.level_test.repository.WordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordService {
    private final WordRepository wordRepository;

    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    // 레벨별 단어 전체 조회
    public List<Word> getWordsByLevel(String level) {
        return wordRepository.findByLevel(level);
    }
}
