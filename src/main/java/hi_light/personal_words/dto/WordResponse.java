package hi_light.personal_words.dto;

import hi_light.personal_words.entity.PersonalWords;
import lombok.AllArgsConstructor;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class WordResponse {
    private Long id;
    private String userId;
    private String wordEn;
    private String koreanMeaning;
    private LocalDate registeredAt;
    private LocalDate nextReviewAt;

    public static WordResponse from(PersonalWords word) {
        return new WordResponse(
                word.getId(),
                word.getUserId(),
                word.getWordEn(),
                word.getKoreanMeaning(),
                word.getRegisteredAt(),
                word.getNextReviewAt()
        );
    }
}

// 서버에서 클라이언트로 보내는 데이터를 담는 용도