package LuckyVicky.backend.pachinko.handler;

import LuckyVicky.backend.pachinko.service.PachinkoService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.JwtTokenUtils;
import LuckyVicky.backend.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
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
        System.out.println("session 안의 요소 개수: " + sessions.size());
        for (WebSocketSession webSocketSession : sessions) {
            System.out.println(webSocketSession.getId());
        }
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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("해당 세션 제거");
        sessions.remove(session);
    }

    private void processSquareSelection(WebSocketSession session, User user, long currentRound, int selectedSquare) {
        System.out.println(pachinkoService.viewSelectedSquares() + "핸들러에서 processSquareSelection 시작지점");

        String result = pachinkoService.selectSquare(user, currentRound, selectedSquare);
        System.out.println(pachinkoService.viewSelectedSquares() + "핸들러에서 processSquareSelection 시작지점");

        if (Objects.equals(result, "정상적으로 선택 완료되었습니다.")) {
            broadcastMessage(user.getNickname() + "가 " + selectedSquare + "을 선택했습니다.");
            checkGameStatusAndCloseSessionsIfNeeded();
        } else if (Objects.equals(result, "다른 사용자가 이전에 선택한 칸입니다.")) {
            sendMessage(session, selectedSquare + "번째 칸은 이미 다른 사용자에 의해 선택되었습니다.");
        } else if (Objects.equals(result, "본인이 이전에 선택한 칸입니다.")) {
            sendMessage(session, selectedSquare + "번째 칸은 본인이 이전에 선택한 칸입니다.");
        } else {
            sendMessage(session, "이미 3칸을 선택하셔서 더 이상 칸을 선택할 수 없습니다.");
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

    private void checkGameStatusAndCloseSessionsIfNeeded() {
        if (pachinkoService.isGameOver()) {
            System.out.println("게임 끝남 확인. 보상 전달 시작");
            broadcastMessage("해당 판이 종료되었습니다. 10초 후 새로운 판이 시작됩니다.");

            Thread rewardThread = new Thread(() -> {
                try {
                    pachinkoService.giveRewards();
                    broadcastMessage("보상 전달이 완료되었습니다.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Thread countdownThread = new Thread(() -> {
                try {
                    countdownAndNotifyPlayers(10);
                    startNewRoundAndNotifyPlayers();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            // 두 작업을 비동기로 병렬 실행
            rewardThread.start();
            countdownThread.start();
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
