package LuckyVicky.backend.global.config;

import LuckyVicky.backend.display_board.handler.DisplayBoardWebSocketHandler;
import LuckyVicky.backend.pachinko.handler.PachinkoWebSocketHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final PachinkoWebSocketHandler pachinkoWebSocketHandler;
    private final DisplayBoardWebSocketHandler displayBoardWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(pachinkoWebSocketHandler, "/pachinko").setAllowedOrigins("*");
        registry.addHandler(displayBoardWebSocketHandler, "/display-board").setAllowedOrigins("*");
    }
}