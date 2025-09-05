package hi_light.user.dto;

import lombok.*;

@Getter
@Setter
public class UserDto {
    private String id;
    private String pw;
    private String name;       // 회원가입 시
    private String nickname;   // 닉네임 변경 시
    private String userRank;   // 레벨테스트 후 업데이트 시
}
