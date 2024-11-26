package LuckyVicky.backend.display_board.handler;

import LuckyVicky.backend.display_board.dto.DisplayMessage;
import LuckyVicky.backend.display_board.repository.DisplayMessageRepository;
import LuckyVicky.backend.display_board.service.DisplayBoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class DisplayBoardWebSocketHandler extends TextWebSocketHandler {
    // 현재 연결된 모든 WebSocket 세션을 저장하는 리스트
    private final List<WebSocketSession> sessions = new ArrayList<>();
    private final DisplayBoardService displayBoardService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("새로운 사용자 접속");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("해당 세션 제거");
        sessions.remove(session);
    }

    @Scheduled(fixedRate = 5000)
    public void broadcastActiveMessages() {
        try {
            // System.out.println("Executing broadcastActiveMessages at " + LocalDateTime.now());

            DisplayMessage message = displayBoardService.getNextDisplayMessage();
            if (message != null) {
                // System.out.println("Broadcasting message: " + message.getContent());

                if (!sessions.isEmpty()) {
                    broadcastMessage(message.getContent());
                } else {
                    // System.out.println("No active WebSocket sessions to broadcast.");
                }
            } else {
                // System.out.println("No active message to broadcast.");
            }

        } catch (Exception e) {
            // System.err.println("Error occurred in broadcastActiveMessages: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void broadcastMessage(String message) {
        for (WebSocketSession session : sessions) {
            sendMessage(session, message);
        }
    }

    private void sendMessage(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
