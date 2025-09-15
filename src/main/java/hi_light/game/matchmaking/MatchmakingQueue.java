package hi_light.game.matchmaking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public class MatchmakingQueue {
    private final Map<String, Queue<String>> queueByRank = new ConcurrentHashMap<>();

    public void addPlayer(String userRank, String userId) {
        log.info("Adding player {} to rank {} queue.", userId, userRank);
        queueByRank.putIfAbsent(userRank, new ConcurrentLinkedQueue<>());
        queueByRank.get(userRank).add(userId);
    }

    public int getQueueSize(String userRank) {
        Queue<String> queue = queueByRank.get(userRank);
        return queue != null ? queue.size() : 0;
    }

    // ⭐ 추가: Matchmaking Service내 에서 랭크 맵을 가져올 수 있는 getter 메서드
    public Map<String, Queue<String>> getQueueByRank() {
        return queueByRank;
    }

    // 2명 이상의 플레이어를 대기열에서 가져와 반환 (원자적)
    public List<String> getPlayersForMatch(String userRank, int minPlayers) {
        Queue<String> queue = queueByRank.get(userRank);
        if (queue == null || queue.size() < minPlayers) {
            return null;
        }

        List<String> players = new ArrayList<>();
        // 한 번의 원자적 작업으로 큐에서 인원을 가져옴
        for (int i = 0; i < minPlayers; i++) {
            String playerId = queue.poll();
            if (playerId != null) {
                players.add(playerId);
            } else {
                // 인원을 가져오는 도중 큐가 비어버리면 가져온 플레이어들을 다시 큐에 넣고 null 반환
                for (String p : players) {
                    queue.add(p);
                }
                return null;
            }
        }
        log.info("Match found for rank {}. Players: {}", userRank, players);
        return players;
    }
}