package hi_light.user.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {
    @Id
    private String id;       // {userId}
    private String pw;       // 비밀번호
    private String name;     // 사용자 이름
    private String nickname; // 닉네임
    private String userRank; // 사용자 등급
}
