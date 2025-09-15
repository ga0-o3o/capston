package hi_light.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getter, Setter, toString 등을 자동으로 생성
@NoArgsConstructor // Lombok이 기본 생성자를 만들어줍니다.
public class JoinRequestDto {
    private String playerId;
    private String nickname;
    private String rank;
    private String token;
}
