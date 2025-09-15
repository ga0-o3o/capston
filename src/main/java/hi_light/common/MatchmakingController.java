package hi_light.common;

import org.springframework.beans.factory.annotation.Autowired;
// Spring이 객체(컴포넌트)를 자동으로 연결해주는 데 사용됩니다.
// 여기서는 MatchmakingService를 컨트롤러에 주입하기 위해 사용됩니다.

import org.springframework.web.bind.annotation.PostMapping;
// HTTP POST 요청을 처리하는 메서드를 지정하는 데 사용됩니다.
// 예를 들어, 클라이언트가 데이터를 보낼 때 사용됩니다.

import org.springframework.web.bind.annotation.RequestParam;
// HTTP 요청의 URL 파라미터를 메서드 파라미터로 바인딩하는 데 사용됩니다.
// 예: /join-match?playerId=abc 에서 'abc'를 가져오는 역할을 합니다.

import org.springframework.web.bind.annotation.RestController;
// 이 클래스가 REST API를 처리하는 컨트롤러임을 나타냅니다.
// 이 어노테이션이 있으면 클래스 내부의 메서드가 반환하는 값이 바로 HTTP 응답 본문이 됩니다.

import org.springframework.web.bind.annotation.RequestBody; // @RequestBody 임포

@RestController
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    @Autowired // Spring이 MatchmakingService를 자동으로 연결해줍니다.
    public MatchmakingController(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }


     // API: POST /join-match
     //플레이어가 매칭 대기열에 참가하는 API입니다.

    @PostMapping("/join-match") // 매칭 요청	  POST	  http://localhost:8080/join-match
    public String joinMatch(
            @RequestBody JoinRequestDto request) { // 클라이언트가 보낸 JSON 데이터를 JoinRequestDto 객체에 자동으로 담아줍니다.

        // 이제 request 객체에서 필요한 정보를 바로 꺼내 쓸 수 있습니다.
        Player player = new Player(
                request.getPlayerId(),
                request.getNickname(),
                request.getRank(),
                request.getToken()
        );

        // 매칭 서비스의 핵심 로직을 호출합니다.
        GameRoom room = matchmakingService.findMatch(player);

        if (room != null) {
            // 매칭 성공 시, 방 ID와 함께 '게임 시작' 신호를 반환합니다.
            return "매칭 성공! 방 ID: " + room.getId();
        } else {
            // 매칭에 실패했다면 '대기 중'임을 알립니다.
            return "매칭 대기 중...";
        }
    }

    //  API: POST /set-ready
     // 플레이어가 게임 시작을 위해 '준비' 상태로 변경하는 API입니다.

    @PostMapping("/set-ready") // 준비 상태 변경	POST	http://localhost:8080/set-ready
    public String setReady(
            @RequestBody ReadyRequestDto request) {

        GameRoom room = matchmakingService.setPlayerReady(request.getRoomId(), request.getPlayerId());
        if (room != null && room.getStatus() == GameRoom.Status.READY) {
            return "모든 플레이어 준비 완료! 아무 플레이어나 시작 버튼을 누르세요!";
        }
        return "준비 완료! 다른 플레이어들을 기다리는 중...";
    }

       // API: POST /start-game
      // 아무 플레이어나 게임을 시작시키는 API입니다.

    @PostMapping("/start-game") // 게임 시작	  POST	  http://localhost:8080/start-game
    public String startGame(
            @RequestBody StartGameRequestDto request) {

        if (matchmakingService.startGame(request.getRoomId())) {
            return "게임이 시작되었습니다!";
        }
        return "게임 시작에 실패했습니다.";
    }
}
