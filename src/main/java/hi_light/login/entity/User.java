package hi_light.login.entity;

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
    private String name;     // 사용자 이름
    private String nickname; // 닉네임
}
