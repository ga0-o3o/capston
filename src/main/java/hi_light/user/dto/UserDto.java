package hi_light.user.dto;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
public class UserDto {
    private String id;
    private String pw;
    private String name;       // 회원가입 시
    @Size(min = 1, max = 12, message = "닉네임은 1자 이상 12자 이하로 입력해주세요.")
    private String nickname;   // 닉네임 변경 시
    private String userRank;   // 레벨테스트 후 업데이트 시
}
