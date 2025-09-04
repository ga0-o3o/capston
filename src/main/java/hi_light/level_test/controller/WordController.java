package hi_light.level_test.controller;

import hi_light.level_test.entity.Word;
import hi_light.level_test.service.WordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/wordbook")
//@CrossOrigin(origins = "*")  // 모든 도메인 허용 (개발용) .. CORS 문제를 해결하는 코드 (브라우저에서)
public class WordController {
    private final WordService wordService;

    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    // 레벨별 단어장 전체 조회
    @GetMapping
    public List<Word> getWordsByLevel(@RequestParam String level) {
        return wordService.getWordsByLevel(level);
    }
}
