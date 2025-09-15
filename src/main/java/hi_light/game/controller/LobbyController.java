package hi_light.game.controller;

import hi_light.game.matchmaking.MatchmakingService;
import hi_light.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/game/lobby")
@RequiredArgsConstructor
public class LobbyController {

    private final MatchmakingService matchmakingService;
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> joinLobby(Authentication authentication) {
        String userId = authentication.getName();
        Optional<String> userRankOptional = userService.getUserRankById(userId);

        if (userRankOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "사용자 랭크를 찾을 수 없습니다."));
        }
        String userRank = userRankOptional.get();

        matchmakingService.joinLobby(userId, userRank);

        return ResponseEntity.ok(Map.of("message", "매칭 대기열에 추가되었습니다."));
    }
}