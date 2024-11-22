package LuckyVicky.backend.global.config;

import LuckyVicky.backend.pachinko.service.PachinkoService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.JwtTokenUtils;
import LuckyVicky.backend.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class PachinkoWebSocketHandler extends TextWebSocketHandler {

    private final PachinkoService pachinkoService;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    // Json 데이터를 처리(파싱)하는 객체
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 현재 연결된 모든 WebSocket 세션을 저장하는 리스트
    private final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("새로운 사용자 접속");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode node = objectMapper.readTree(payload);

        // JWT 토큰이 없는 경우 처리 (첫 번째 메시지에만 JWT 필수)
        if (!session.getAttributes().containsKey("user") && node.has("token")) {
            String token = node.get("token").asText();
            if (jwtTokenUtils.validateToken(token)) {
                String username = jwtTokenUtils.getUsernameFromToken(token);
                User user = userService.findByUserName(username);
                session.getAttributes().put("user", user);
            } else {
                sendMessage(session, "JWT 검증 실패하여 연결 종료합니다.");
                session.close();
                return;
            }
        }

        // 칸 선택 처리 (JWT 검증이 완료된 후)
        if (!node.has("square")) {
            sendMessage(session, "Invalid message format: 'square' 필드에 값이 없습니다.");
            return;
        }

        int selectedSquare = node.get("square").asInt();
        User user = (User) session.getAttributes().get("user");

        if (user == null) {
            sendMessage(session, "유저가 인증되지 않았습니다.");
            return;
        }

        long currentRound = pachinkoService.getCurrentRound();

        if (!validateUserState(session, user, currentRound)) {
            return;
        }

        processSquareSelection(session, user, currentRound, selectedSquare);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        System.out.println("해당 세션 제거");
        sessions.remove(session);
    }

    private void processSquareSelection(WebSocketSession session, User user, long currentRound, int selectedSquare) {
        if (pachinkoService.selectSquare(user, currentRound, selectedSquare)) {
            broadcastMessage(user.getUsername() + "가 " + selectedSquare + "을 선택했습니다.");
            checkGameStatusAndFinishGameIfNeeded();
        } else {
            sendMessage(session, selectedSquare + "번째 칸은 이미 다른 사용자에 의해 선택되었습니다.");
        }
    }

    private boolean validateUserState(WebSocketSession session, User user, long currentRound) {
        if (pachinkoService.noMoreJewel(user)) {
            sendMessage(session, "칸을 선택할때 필요한 보석이 부족합니다.");
            return false;
        }

        if (!pachinkoService.canSelectMore(user, currentRound)) {
            sendMessage(session, "이미 3칸을 선택하셔서 더 이상 칸을 선택할 수 없습니다.");
            return false;
        }

        return true;
    }

    private void sendMessage(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkGameStatusAndFinishGameIfNeeded() {
        if (pachinkoService.isGameOver()) {
            System.out.println("게임 끝남 확인. 보상 전달 시작");
            broadcastMessage("해당 판이 종료되었습니다. 10초 후 새로운 판이 시작됩니다.");
            pachinkoService.giveRewards();
            broadcastMessage("보상 전달이 완료되었습니다.");

            new Thread(() -> {
                try {
                    countdownAndNotifyPlayers(10);
                    startNewRoundAndNotifyPlayers();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void countdownAndNotifyPlayers(int seconds) throws InterruptedException {
        for (int i = seconds; i > 0; i--) {
            broadcastMessage(i + "초 후에 새로운 게임이 시작됩니다.");
            Thread.sleep(1000);
        }
    }

    private void startNewRoundAndNotifyPlayers() {
        broadcastMessage("새로운 판이 시작됩니다.");
        pachinkoService.startNewRound();
    }

    private void broadcastMessage(String message) {
        for (WebSocketSession session : sessions) {
            sendMessage(session, message);
        }
    }

    /*public void endGameForAll() {
        List<WebSocketSession> sessionsCopy = new ArrayList<>(sessions);
        for (WebSocketSession session : sessionsCopy) {
            sendMessage(session, "해당 게임의 세션을 모두 종료합니다.");
            try {
                session.close(CloseStatus.NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sessions.clear();
    }*/
}
