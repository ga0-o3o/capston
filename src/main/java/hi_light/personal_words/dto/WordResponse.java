package hi_light.personal_words.dto;

import hi_light.personal_words.entity.PersonalWords;
import lombok.Builder;
//import lombok.AllArgsConstructor;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
//@AllArgsConstructor
@Builder
public class WordResponse {
    private Long id;
    private Integer displayId; // 사용자에게 보여줄 순서 번호
    private String userId;
    private String wordEn;
    private String koreanMeaning;
    private LocalDate registeredAt;
    private LocalDate nextReviewAt;

    public static WordResponse from(PersonalWords word) {
        return WordResponse.builder()
                .id(word.getId())
                .userId(word.getUserId())
                .wordEn(word.getWordEn())
                .koreanMeaning(word.getKoreanMeaning())
                .registeredAt(word.getRegisteredAt())
                .nextReviewAt(word.getNextReviewAt())
                .build();
    }
}

// 서버에서 클라이언트로 보내는 데이터를 담는 용도