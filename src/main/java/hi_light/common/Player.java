package hi_light.common;

import lombok.Data;

/**
 * 게임 플레이어 정보를 담는 데이터 클래스입니다.
 * 모든 게임에서 공통으로 사용됩니다.
 */

@Data
public class Player {
    private String id; // 플레이어를 식별하는 고유 ID (서버 내부용)
    private String nickname; // 게임 내에서 보여질 닉네임 (중복 가능)
    private String rank; // 플레이어의 어휘력 레벨
    private String token; // 로그인 상태를 유지하는 인증 토큰

    public Player(String id, String nickname, String rank, String token) {
        this.id = id;
        this.nickname = nickname;
        this.rank = rank;
        this.token = token;
    }
}
