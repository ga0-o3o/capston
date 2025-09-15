package hi_light.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StartGameRequestDto {
    private String roomId;
    private String playerId;
}
