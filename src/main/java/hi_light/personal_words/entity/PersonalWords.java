package hi_light.personal_words.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "personal_words")

public class PersonalWords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "word_en", nullable = false)
    private String wordEn;

    @Column(name = "korean_meaning", nullable = false)
    private String koreanMeaning;

    @Column(name = "registered_at", nullable = false)
    private LocalDate registeredAt;

    @Column(name = "next_review_at", nullable = false)
    private LocalDate nextReviewAt;

    // 에빙하우스 망각 곡선에 따른 복습 주기를 계산하기 위해
    @Column(name = "review_count")
    private int reviewCount = 0;
}
