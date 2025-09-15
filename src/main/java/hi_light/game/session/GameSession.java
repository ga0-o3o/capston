package hi_light.game.session;

import lombok.Getter;
import java.util.List;
import java.util.UUID;

@Getter
public class GameSession {
    private final String sessionId;
    private final List<String> playerIds;

    public GameSession(List<String> playerIds) {
        this.sessionId = UUID.randomUUID().toString();
        this.playerIds = playerIds;
    }
}