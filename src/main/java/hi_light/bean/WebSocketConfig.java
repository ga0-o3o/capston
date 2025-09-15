package hi_light.bean;

import hi_light.game.matchmaking.MatchmakingWebSocketHandler;
import hi_light.interceptor.WebSocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final MatchmakingWebSocketHandler matchmakingWebSocketHandler;
    private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    // 2. addInterceptors에 새로운 인스턴스를 생성하지 않고 주입받은 객체를 사용
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(matchmakingWebSocketHandler, "/ws/matchmaking")
                .addInterceptors(webSocketHandshakeInterceptor)
                .setAllowedOrigins("http://localhost:57242"); // 프론트와 맞게!! 수정하기 운영 환경에 맞게 변경하세요.
    }
}