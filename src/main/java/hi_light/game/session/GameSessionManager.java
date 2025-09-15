package hi_light.game.session;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class GameSessionManager {
    private final Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();

    public GameSession createSession(List<String> playerIds) {
        GameSession session = new GameSession(playerIds);
        activeSessions.put(session.getSessionId(), session);
        return session;
    }

    public GameSession getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }
}