package LuckyVicky.backend.global.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

// 웹소켓의 전반적인 설정
@Configuration
@EnableWebSocket // spring에서 웹소켓 지원 활성화
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer { // 인터페이스 구현하여 웹소켓 핸들러 등록
    private final PachinkoWebSocketHandler pachinkoWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(pachinkoWebSocketHandler, "/pachinko").setAllowedOrigins("*");
        // 웹소켓 엔드포인트 /pachinko를 설정하고, pachinkoWebSocketHandler를 해당 엔드포인트에서 실행될 핸들러로 지정
    }
}