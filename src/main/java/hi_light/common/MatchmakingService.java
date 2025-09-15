package hi_light.common;

// Spring이 이 클래스를 자동으로 관리하고 다른 곳에서 사용할 수 있게 해줍니다.
import org.springframework.stereotype.Service; // 이 클래스가 Spring의 서비스 컴포넌트임을 나타냅니다.

import java.util.Queue; // 대기열 자료구조를 사용하기 위한 인터페이스를 가져옵니다.

// 여러 스레드가 동시에 데이터에 접근해도 충돌이 일어나지 않도록 내부적으로 동기화를 처리합니다.
import java.util.concurrent.ConcurrentHashMap; // 멀티스레드 환경에서 안전하게 사용할 수 있는 해시 맵입니다.

// 플레이어를 대기열에 추가하거나 제거하는 작업을 여러 스레드가 동시에 해도 안전합니다.
import java.util.concurrent.ConcurrentLinkedQueue; // 멀티스레드 환경에서 안전하게 사용할 수 있는 큐(Queue)입니다.

// 게임방의 ID처럼 중복되지 않는 값을 만들 때 사용됩니다.
import java.util.UUID; // 고유한 식별자(ID)를 생성하기 위한 클래스입니다.

/**
 * 플레이어 매칭과 게임방 관리를 담당하는 핵심 서비스입니다.
 * 동시 접속하는 여러 플레이어를 효율적으로 처리하며, 데이터의 일관성을 유지합니다.
 */

@Service
public class MatchmakingService {

     //  waitingPlayers라는 이름의 대기실을 만드는데, 레벨(문자열)별로 플레이어(Player)를 줄 세워 놓는 자료구조
    private final ConcurrentHashMap<String, Queue<Player>> waitingPlayers = new ConcurrentHashMap<>();
    // activeRooms는 매칭이 완료되어 게임이 진행 중인 방들의 목록을 관리하는 자료구조
    private final ConcurrentHashMap<String, GameRoom> activeRooms = new ConcurrentHashMap<>();

    /**
     * 플레이어를 매칭 대기열에 추가하고, 조건이 맞으면 게임방을 생성합니다.
     * @param player 매칭을 요청한 플레이어 객체
     * @return 매칭 성공 시 생성된 GameRoom 객체, 실패 시 null
     */

    public GameRoom findMatch(Player player) {
        waitingPlayers.putIfAbsent(player.getRank(), new ConcurrentLinkedQueue<>()); // 레벨별 대기열 확인 및 생성
        Queue<Player> queue = waitingPlayers.get(player.getRank());
        queue.add(player); // 대기열에 플레이어 추가

        // 매칭 조건 확인 및 게임방 생성
        // 대기열에 플레이어가 2명 이상 모였는지 확인합니다. 이 조건이 충족되면 매칭이 성공한 것으로 간주하고, 새로운 게임방(GameRoom)을 생성합니다.
        if (queue.size() >= 2) {
            GameRoom room = new GameRoom();
            room.setId(UUID.randomUUID().toString());

            // 대기열에 있는 플레이어들을 한 명씩 꺼내서, 새로 만든 게임방으로 옮겨 담습니다. 이 과정은 게임방의 최대 인원(5명)이 찰 때까지 계속됩니다.
            while (room.getPlayers().size() < 5 && !queue.isEmpty()) {
                room.addPlayer(queue.poll());
            }
            activeRooms.put(room.getId(), room);
            // 새로 만들어진 게임방을 activeRooms 맵에 등록하고, 이 게임방 객체를 반환합니다. 이 반환된 객체를 받은 컨트롤러는 클라이언트에게 **"매칭 성공, 게임 시작!"**이라는 신호를 보냅니다.
            return room;
        }
        // 대기열에 2명 미만의 플레이어만 있다면, 함수는 null을 반환하고 플레이어는 계속해서 매칭을 기다리게 됩니다.
        return null;
    }

    /**
     * 플레이어의 준비 상태를 변경합니다.
     * @param roomId 플레이어가 속한 게임방의 ID
     * @param playerId 준비 상태를 변경할 플레이어의 ID
     * @return 상태가 변경된 GameRoom 객체, 실패 시 null
     */

    public GameRoom setPlayerReady(String roomId, String playerId) {

        // roomId를 사용하여 활성화된 게임방(GameRoom) 객체를 찾습니다.
        GameRoom room = activeRooms.get(roomId);

        // 방이 존재하지 않거나, 이미 게임이 시작된 상태라면 처리를 중단합니다.
        if (room == null || room.getStatus() != GameRoom.Status.WAITING) {
            return null;
        }

        // 3. **동기화 (Synchronization) 시작:**
        // 여러 플레이어가 거의 동시에 '준비' 버튼을 누를 수 있습니다.
        // 이 경우, 여러 스레드가 동시에 이 코드 블록에 진입하게 됩니다.
        // 'synchronized (room)'은 이 방 객체(room)에 잠금(Lock)을 걸어,
        // 한 번에 한 스레드만 내부 코드를 실행하도록 합니다.
        synchronized (room) {

            // --- 디버깅 코드 시작 ---
            System.out.println("플레이어 " + playerId + "가 준비 상태로 전환 요청함.");
            System.out.println("현재 방의 플레이어 수: " + room.getPlayers().size());
            System.out.println("현재 플레이어 준비 상태: " + room.getPlayerReadyStatus());
            // --- 디버깅 코드 끝 ---

            // 플레이어의 준비 상태를 'true'로 변경합니다.
            room.getPlayerReadyStatus().put(playerId, true);

            // --- 디버깅 코드 시작 ---
            System.out.println("변경 후 플레이어 준비 상태: " + room.getPlayerReadyStatus());
            System.out.println("isRoomReady 결과: " + isRoomReady(room));
            // --- 디버깅 코드 끝 ---

            // 모든 플레이어가 준비되었는지 확인합니다.
            if (isRoomReady(room)) {
                // 모든 플레이어가 준비되었다면, 방의 상태를 'READY'로 변경합니다.
                room.setStatus(GameRoom.Status.READY);
            }
            // 변경된 게임방 객체를 반환합니다.
            return room;
        }
    }

    private boolean isRoomReady(GameRoom room) {
        // 방에 최소 인원(2명)이 있는지 확인합니다. 그리고, 모든 플레이어의 준비 상태가 'true'인지 확인합니다.
        return room.getPlayers().size() >= 2 && room.getPlayerReadyStatus().values().stream().allMatch(ready -> ready);
    }

    /**
     * 방장이 게임 시작을 요청했을 때 게임 상태를 변경합니다.
     * @param roomId 게임방 ID
     * @return 게임 시작 성공 여부
     */
    public boolean startGame(String roomId) {
        // roomId로 현재 게임방(GameRoom)을 찾습니다.
        GameRoom room = activeRooms.get(roomId);

        //  **게임 시작 조건 확인**:
        //    - 방이 존재하는지 확인합니다 (room == null).
        //    - 요청을 보낸 플레이어가 방장(host)인지 확인합니다 (!room.getHostId().equals(playerId)).
        //    - 방의 현재 상태가 '준비 완료(READY)'인지 확인합니다 (room.getStatus() != GameRoom.Status.READY).
        //    위 세 가지 조건 중 하나라도 만족하지 않으면 게임을 시작할 수 없습니다.
        if (room == null || room.getStatus() != GameRoom.Status.READY) {
            return false; // 조건을 만족하지 못해 게임 시작 실패
        }

        //  **게임 상태 변경**:
        //    - 모든 조건이 충족되면, 방의 상태를 '게임 진행 중(IN_GAME)'으로 변경합니다.
        room.setStatus(GameRoom.Status.IN_GAME);

        // 게임 시작 성공을 반환합니다.
        return true;
    }

}
