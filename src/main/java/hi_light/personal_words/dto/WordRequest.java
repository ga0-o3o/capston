package hi_light.personal_words.dto;

import lombok.*;

@Getter
@Setter
public class WordRequest {
    private String userId;
    private String wordEn;
    private String koreanMeaning;
}

// 클라이언트로(프론트엔드 또는 파이썬 스크립트)로부터 서버로 들어오는 데이터를 담는 용도
// 서버의 컨트롤러는 이 데이터를 받아 WordRequest 객체로 변환합니다.