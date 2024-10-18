package LuckyVicky.backend.global.config;

import LuckyVicky.backend.pachinko.service.PachinkoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.ArrayList;
import java.util.List;

// 클라이언트와의 상호작용을 관리
@Component
@RequiredArgsConstructor
public class PachinkoWebSocketHandler extends TextWebSocketHandler {

    private List<WebSocketSession> sessions = new ArrayList<>();
    private final PachinkoService pachinkoService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 새로운 사용자가 연결되었을 때
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메시지를 받을 때
        String userMessage = message.getPayload();
        int selectedSquare = Integer.parseInt(userMessage); // 칸 번호를 받음

        // 서비스 계층에서 칸 선택 처리
        boolean isValidSelection = pachinkoService.selectSquare(selectedSquare);

        // 선택이 유효하면 모든 클라이언트에게 업데이트
        if (isValidSelection) {
            for (WebSocketSession webSocketSession : sessions) {
                webSocketSession.sendMessage(new TextMessage("Square " + selectedSquare + " selected."));
            }
        } else {
            // 선택이 유효하지 않으면 해당 클라이언트에 알림
            session.sendMessage(new TextMessage("Square " + selectedSquare + " is already selected."));
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 연결이 종료되었을 때
        sessions.remove(session);
    }
}

