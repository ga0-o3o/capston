package hi_light.user.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.Size;

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
    @Size(min = 1, max = 12, message = "닉네임은 1자 이상 12자 이하로 입력해주세요.")
    @Column(length = 12)
    private String nickname; // 닉네임
    private String userRank; // 사용자 등급
}
