package hi_light.bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
// STOMP(Simple Text Oriented Messaging Protocol) 기반의 메시지 브로커를 활성화합니다.
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * 클라이언트가 웹소켓 서버에 연결할 엔드포인트를 등록합니다.
     * 클라이언트는 '/ws' 주소로 웹소켓 연결을 시도하게 됩니다.
     */
    // 클라이언트가 서버에 연결하는 '주소'를 정의합니다. 클라이언트는 이 주소(/ws)로 접속해야 웹소켓 연결을 시작할 수 있습니다. 마치 여러분의 게임 서버로 전화를 걸 수 있는 번호(ws://localhost:8080/ws)를 지정하는 것과 같습니다.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    /**
     * 메시지 브로커를 설정합니다.
     * 클라이언트에게 메시지를 전달할 경로와, 클라이언트로부터 메시지를 받을 경로를 정의합니다.
     */
    // 메시지 전달 규칙을 정의합니다. 이 설정 덕분에 서버와 클라이언트가 서로 메시지를 주고받을 수 있습니다.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // '/topic'으로 시작하는 경로를 구독하는 클라이언트에게 메시지를 전달합니다.
        // 예: '/topic/game/123'을 구독하는 모든 플레이어에게 메시지를 보냅니다.
        registry.enableSimpleBroker("/topic");
        // 서버가 클라이언트에게 메시지를 보낼 때 사용하는 '방송 채널'을 만듭니다. /topic으로 시작하는 모든 메시지는 이 채널을 구독하는 모든 클라이언트에게 전달됩니다.

        // '/app'으로 시작하는 메시지는 컨트롤러로 라우팅됩니다.
        // 클라이언트가 서버에 메시지를 보낼 때 사용합니다.
        registry.setApplicationDestinationPrefixes("/app");
        //  클라이언트가 서버에게 메시지를 보낼 때 사용하는 '직통 전화'를 정의합니다. 클라이언트가 /app으로 시작하는 메시지를 보내면, 이 메시지는 서버의 특정 컨트롤러로 전달됩니다.
    }
}
