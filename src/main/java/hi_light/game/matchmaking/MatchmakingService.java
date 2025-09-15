package hi_light.game.matchmaking;

import hi_light.game.session.GameSession;
import hi_light.game.session.GameSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchmakingService {
    private final MatchmakingQueue matchmakingQueue;
    private final GameSessionManager gameSessionManager;
    private final MatchmakingWebSocketHandler matchmakingWebSocketHandler;

    public void joinLobby(String userId, String userRank) {
        matchmakingQueue.addPlayer(userRank, userId);
    }

    // 5초마다 모든 랭크의 큐에서 매칭을 시도하는 스케줄러
    @Scheduled(fixedDelay = 5000)
    public void matchPlayersInAllQueues() {
        // 모든 랭크 키를 가져와 순회
        matchmakingQueue.getQueueByRank().keySet().forEach(userRank -> {
            tryMatchPlayers(userRank)
                    .ifPresent(this::notifyMatchFound);
        });
    }

    public Optional<GameSession> tryMatchPlayers(String userRank) {
        List<String> players = matchmakingQueue.getPlayersForMatch(userRank, 2);
        if (players != null) {
            GameSession newSession = gameSessionManager.createSession(players);
            return Optional.of(newSession);
        }
        return Optional.empty();
    }

    public void notifyMatchFound(GameSession session) {
        try {
            for (String playerId : session.getPlayerIds()) {
                matchmakingWebSocketHandler.sendMessageToUser(playerId, "Match found! Session ID: " + session.getSessionId());
            }
        } catch (IOException e) {
            log.error("Failed to send WebSocket message", e);
        }
    }
}