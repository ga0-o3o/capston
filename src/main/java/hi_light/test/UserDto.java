package hi_light.test;


import hi_light.naver_login.entity.User;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserDto {
    private String id;
    private String name;
    private String nickname;

    public static UserDto from(User u) {
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .nickname(u.getNickname())
                .build();
    }
}
