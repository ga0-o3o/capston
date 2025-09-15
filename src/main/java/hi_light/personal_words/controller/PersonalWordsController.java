package hi_light.personal_words.controller;

import hi_light.personal_words.dto.WordRequest;
import hi_light.personal_words.dto.WordResponse;
import hi_light.personal_words.entity.PersonalWords;
import hi_light.personal_words.service.PersonalWordsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal-words")
public class PersonalWordsController {

    private final PersonalWordsService personalWordsService;

    // 새 단어 추가 API (파이썬 스크립트 또는 직접 입력)

    @PostMapping("/add")
    public ResponseEntity<WordResponse> addWord(@RequestBody WordRequest request) {
        PersonalWords newWord = personalWordsService.addWord(request);
        return ResponseEntity.ok(WordResponse.from(newWord)); // ok 부분이 HTTP 상태 코드 200을 의미하는 약속된 메서드.
    }

    // 복습일 업데이트 API
    @PutMapping("/review/{wordId}/{userId}")
    public ResponseEntity<WordResponse> completeReview(@PathVariable Long wordId, @PathVariable String userId) {
        PersonalWords updatedWord = personalWordsService.updateNextReviewDate(wordId, userId);
        return ResponseEntity.ok(WordResponse.from(updatedWord));
    }

    // 단어 수정 API
    @PutMapping("/{wordId}")
    public ResponseEntity<WordResponse> updateWord(@PathVariable Long wordId, @RequestBody WordRequest request) {
        PersonalWords updatedWord = personalWordsService.updateWord(wordId, request);
        return ResponseEntity.ok(WordResponse.from(updatedWord));
    }

    // 단어 삭제 API
    @DeleteMapping("/{wordId}/{userId}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long wordId, @PathVariable String userId) {
        personalWordsService.deleteWord(wordId, userId);
        return ResponseEntity.ok().build();
    }

    // 사용자 단어 목록 조회 API
    @GetMapping("/{userId}")
    public ResponseEntity<List<WordResponse>> getWordsByUserId(@PathVariable String userId) {
        // 반환 타입과 일치하도록 변수 타입을 변경합니다.
        List<WordResponse> words = personalWordsService.getWordsByUserId(userId);

        // 서비스에서 이미 DTO를 반환하므로, 변환 로직은 제거합니다.
        return ResponseEntity.ok(words);
    }

    // 여러 사용자들의 단어장 전송 API
    @PostMapping("/for-game")
    public ResponseEntity<List<WordResponse>> getWordsForGame(@RequestBody List<String> userIds) {

        List<WordResponse> words = personalWordsService.getWordsForGame(userIds);

        return ResponseEntity.ok(words);
    }
}
