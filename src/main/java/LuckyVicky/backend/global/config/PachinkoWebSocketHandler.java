package LuckyVicky.backend.global.config;

import LuckyVicky.backend.pachinko.service.PachinkoService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.JwtTokenUtils;
import LuckyVicky.backend.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final PachinkoService pachinkoService;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    // ObjectMapper 필드로 선언하여 재사용
    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 새로운 사용자가 연결되었을 때
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode node = objectMapper.readTree(payload);

        // JWT 토큰이 없는 경우도 처리 (첫 번째 메시지에만 JWT 필수) -> 프론트가 연결하자마자 토큰 줘야함
        if (!session.getAttributes().containsKey("user") && node.has("token")) {
            String token = node.get("token").asText();
            if (jwtTokenUtils.validateToken(token)) {
                String username = jwtTokenUtils.getUsernameFromToken(token);
                User user = userService.findByUserName(username);
                // 사용자 정보를 세션에 저장
                session.getAttributes().put("user", user);

            } else {
                session.close(); // JWT 검증 실패 시 연결 종료
                return; // 이후 코드 실행 중단
            }
        }

        // 칸 선택 처리 (JWT 검증이 완료된 후)
        if (node.has("square")) {  // JSON에 "square" 필드가 있는지 확인
            int selectedSquare = node.get("square").asInt();  // "square" 필드의 값을 추출
            User user = (User) session.getAttributes().get("user");

            if (user != null) {
                long currentRound = pachinkoService.getCurrentRound();
                boolean isValidSelection = pachinkoService.selectSquare(user, currentRound, selectedSquare);
                if (isValidSelection) {
                    for (WebSocketSession webSocketSession : sessions) {
                        webSocketSession.sendMessage(new TextMessage("User " + user.getUsername() + " selected square " + selectedSquare));
                    }
                } else {
                    session.sendMessage(new TextMessage("Square " + selectedSquare + " is already selected."));
                }
            } else {
                session.sendMessage(new TextMessage("User is not authenticated.")); // 예외 처리
            }
        } else {
            session.sendMessage(new TextMessage("Invalid message format: 'square' field is missing."));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 연결이 종료되었을 때
        sessions.remove(session);
    }
}
