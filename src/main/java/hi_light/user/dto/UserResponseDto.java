package hi_light.user.dto;

import hi_light.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String id;
    private String name;
    private String nickname;
    private String userRank;

    // User 엔티티를 DTO로 변환하는 생성자
    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.userRank = user.getUserRank();
    }
}
