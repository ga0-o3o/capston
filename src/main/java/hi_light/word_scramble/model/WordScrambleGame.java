package hi_light.word_scramble.model;

import hi_light.common.Player;
import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

// 이 파일은 게임의 현재 상태를 저장하는 '게임판'입니다. 누가 몇 점을 얻었고, 어떤 순서로 맞혔는지 등의 정보를 담습니다.

@Data
public class WordScrambleGame {
    private String id;
    private List<Player> players;
    private String promptWord; // 제시어
    private boolean isFinished = false; // 게임 종료 여부
    private int totalPlayers; // 게임 시작 시 총 플레이어 수 (점수 계산에 사용)

    // **동기화**: 플레이어별 점수. 여러 스레드가 동시에 접근하므로 안전한 맵을 사용합니다.
    private ConcurrentHashMap<String, Integer> scores;

    // **동기화**: 정답을 맞힌 순서. 먼저 맞힌 플레이어가 큐의 맨 앞에 위치합니다.
    private Queue<String> submissionOrder = new ConcurrentLinkedQueue<>();

    public WordScrambleGame(String id, List<Player> players, String promptWord) {
        this.id = id;
        this.players = players;
        this.promptWord = promptWord;
        this.totalPlayers = players.size();
        this.scores = new ConcurrentHashMap<>();
        players.forEach(p -> scores.put(p.getId(), 0)); // 모든 플레이어 점수 0으로 초기화
    }
}
