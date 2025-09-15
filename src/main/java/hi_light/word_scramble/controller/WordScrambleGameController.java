package hi_light.word_scramble.controller;

import hi_light.common.Player;
import hi_light.word_scramble.service.WordScrambleGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * "단어 빨리 맞추기" 게임의 API 요청을 처리하는 컨트롤러입니다.
 * 클라이언트의 답안 제출 요청을 받아 게임 서비스로 전달합니다.
 */

@RestController
@RequestMapping("/api/word-scramble") // 이 컨트롤러의 기본 URL 경로
public class WordScrambleGameController {
    private final WordScrambleGameService gameService;

    @Autowired
    public WordScrambleGameController(WordScrambleGameService gameService) {
        this.gameService = gameService;
    }

    /**
     * API: POST /api/word-scramble/start
     * 게임 시작 신호를 받아 게임 로직을 초기화합니다.
     * 이 API는 매칭이 완료된 후 백엔드 내부에서 호출됩니다.
     * @param gameId 게임방 ID
     * @param players 게임에 참여하는 플레이어 목록
     * @return 게임 시작 성공 메시지
     */
    @PostMapping("/start")
    public String startGame(@RequestParam String gameId, @RequestBody List<Player> players) {
        // 이 API는 매칭 서비스에서 게임방이 만들어진 후 호출됩니다.
        gameService.startGame(gameId, players);
        return "게임 로직이 시작되었습니다.";
    }

    /**
     * API: POST /api/word-scramble/submit
     * 플레이어가 답안을 제출할 때 호출되는 API입니다.
     * @param gameId 플레이어가 속한 게임방 ID
     * @param playerId 답안을 제출한 플레이어의 ID
     * @param answer 제출된 답안
     * @return 답안 처리 결과를 알리는 메시지
     */
    @PostMapping("/submit")
    public String submitAnswer(@RequestParam String gameId, @RequestParam String playerId, @RequestParam String answer) {
        gameService.submitAnswer(gameId, playerId, answer);
        return "답안 제출 완료";
    }
}
