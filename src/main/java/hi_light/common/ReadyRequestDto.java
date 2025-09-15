package hi_light.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadyRequestDto {
    private String roomId;
    private String playerId;
}
