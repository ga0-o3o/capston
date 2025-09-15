package hi_light.word_scramble.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import hi_light.word_scramble.service.WordScrambleGameService;

import java.util.Map;

/**
 * 웹소켓을 통해 클라이언트로부터 메시지를 받는 컨트롤러입니다.
 * 클라이언트의 실시간 행동(예: 답안 제출)을 처리합니다.
 */
@Controller
public class WebSocketController {
    private final WordScrambleGameService gameService;

    public WebSocketController(WordScrambleGameService gameService) {
        this.gameService = gameService;
    }

    /**
     * 클라이언트가 /app/submit.answer 경로로 메시지를 보낼 때 이 메서드가 실행됩니다.
     * @param message 클라이언트가 보낸 메시지 (JSON 형태로 파싱됩니다)
     * @return 특정 경로를 구독하는 클라이언트에게 응답을 보낼 수 있습니다.
     */
    @MessageMapping("/submit.answer") // 클라이언트가 보내는 메시지 경로
    public void submitAnswer(Map<String, String> message) {
        // 클라이언트가 보낸 메시지에서 게임방 ID, 플레이어 ID, 답안을 추출합니다.
        String gameId = message.get("gameId");
        String playerId = message.get("playerId");
        String answer = message.get("answer");

        // 게임 서비스의 핵심 로직을 호출합니다.
        gameService.submitAnswer(gameId, playerId, answer);
    }
}
