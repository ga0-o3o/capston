package hi_light.common;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // 내부적으로 동기화 기능을 제공하는 특별한 맵

/**
 * 게임 세션(방)의 상태를 관리하는 데이터 클래스입니다.
 * 모든 게임에서 공통으로 사용되며, 멀티스레드 환경에서 안전하게 접근 가능해야 합니다.
 * 여러 플레이어가 동시에 이 방의 상태를 변경할 수 있기 때문에 동기화가 중요합니다.
 */

@Data
public class GameRoom {

    // 게임방의 현재 상태를 나타내는 열거형
    public enum Status {
        WAITING, // 대기 중 (인원 부족 또는 준비 안 됨)
        READY,   // 시작 준비 완료 (모든 플레이어가 준비됨)
        IN_GAME  // 게임 진행 중
    }

    private String id; // 게임 방의 고유 ID
    private List<Player> players = new ArrayList<>(); // 이 방에 속한 플레이어들
    private Status status = Status.WAITING; // 게임방의 현재 상태

    // **동기화:** 플레이어별 '준비' 상태를 저장하는 맵
    // 여러 스레드가 동시에 접근하므로 ConcurrentHashMap을 사용해 안전하게 관리합니다.
    private Map<String, Boolean> playerReadyStatus = new ConcurrentHashMap<>();

    /**
     * 플레이어를 게임방에 추가하는 메서드입니다.
     * @param player 게임방에 들어온 플레이어 객체
     */

    public void addPlayer(Player player) {
        this.players.add(player);
        // 플레이어가 방에 들어오면 기본적으로 '준비 안 됨'으로 설정합니다.
        playerReadyStatus.put(player.getId(), false);
    }

}
