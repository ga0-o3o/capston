package hi_light.level_test.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "words")
@Data              // getter, setter, toString 등 자동 생성
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 가진 생성자

public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String wordEn;

    @Column(nullable = false)
    private String koreanMeaning;

    @Column(nullable = false)
    private String partOfSpeech;

    @Column(nullable = false)
    private String level;
}