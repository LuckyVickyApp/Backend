package LuckyVicky.backend.pachinko.handler;

import static LuckyVicky.backend.pachinko.service.PachinkoService.PACHINKO_USER_MAX_SQUARES;

import LuckyVicky.backend.pachinko.service.PachinkoService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.JwtTokenUtils;
import LuckyVicky.backend.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class PachinkoWebSocketHandler extends TextWebSocketHandler {

    private final PachinkoService pachinkoService;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<WebSocketSession> sessions = new ArrayList<>();
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final Semaphore messageLimiter = new Semaphore(200); // 동시에 200개만 처리

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        logSessionConnected();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        virtualThreadExecutor.submit(() -> {
            try {
                processIncomingMessage(session, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    private void processIncomingMessage(WebSocketSession session, TextMessage message) throws IOException {
        JsonNode node = objectMapper.readTree(message.getPayload());

        if (!validateJwt(session, node)) {
            return;
        }
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

    private boolean validateJwt(WebSocketSession session, JsonNode node) throws IOException {
        if (!session.getAttributes().containsKey("user") && node.has("token")) {
            String token = node.get("token").asText();
            if (jwtTokenUtils.validateToken(token)) {
                String username = jwtTokenUtils.getUsernameFromToken(token);
                User user = userService.findByUserName(username);
                session.getAttributes().put("user", user);
            } else {
                sendMessage(session, "JWT 검증 실패하여 연결 종료합니다.");
                session.close();
                return false;
            }
        }
        return true;
    }

    private boolean validateUserState(WebSocketSession session, User user, long currentRound) {
        if (pachinkoService.noMoreJewel(user)) {
            sendMessage(session, "칸을 선택할때 필요한 보석이 부족합니다.");
            return false;
        }
        if (!pachinkoService.canSelectMore(user, currentRound)) {
            sendMessage(session, "이미 " + PACHINKO_USER_MAX_SQUARES + "칸을 선택하셔서 더 이상 칸을 선택할 수 없습니다.");
            return false;
        }
        return true;
    }

    private void processSquareSelection(WebSocketSession session, User user, long currentRound, int selectedSquare) {
        String result = pachinkoService.selectSquare(user, currentRound, selectedSquare);
        switch (result) {
            case "정상적으로 선택 완료되었습니다." -> {
                broadcastMessage(user.getNickname() + "가 " + selectedSquare + "을 선택했습니다.");
                checkGameStatusAndCloseSessionsIfNeeded();
            }
            case "다른 사용자가 이전에 선택한 칸입니다." -> sendMessage(session, selectedSquare + "번째 칸은 이미 다른 사용자에 의해 선택되었습니다.");
            case "본인이 이전에 선택한 칸입니다." -> sendMessage(session, selectedSquare + "번째 칸은 본인이 이전에 선택한 칸입니다.");
            case null, default -> sendMessage(session, "이미 3칸을 선택하셔서 더 이상 칸을 선택할 수 없습니다.");
        }
    }

    private void checkGameStatusAndCloseSessionsIfNeeded() {
        if (pachinkoService.isGameOver()) {
            broadcastMessage("해당 판이 종료되었습니다. 10초 후 새로운 판이 시작됩니다.");

            Thread.startVirtualThread(() -> {
                try {
                    pachinkoService.giveRewards();
                    broadcastMessage("보상 전달이 완료되었습니다.");
                    pachinkoService.startNewRound();
                    log.info("새로운 판 준비가 완료되었습니다.");
                } catch (Exception e) {
                    log.error("보상 처리 중 예외 발생", e);
                }
            });

            Thread.startVirtualThread(() -> {
                try {
                    countdownAndNotifyPlayers(10);
                    broadcastMessage("새로운 판이 시작됩니다.");
                } catch (InterruptedException e) {
                    log.error("카운트다운 중 예외 발생", e);
                }
            });
        }
    }

    private void countdownAndNotifyPlayers(int seconds) throws InterruptedException {
        for (int i = seconds; i > 0; i--) {
            broadcastMessage(i + "초 후에 새로운 게임이 시작됩니다.");
            Thread.sleep(1000); // 가상 쓰레드라 문제 없음
        }
    }

    private void broadcastMessage(String message) {
        for (WebSocketSession session : sessions) {
            sendMessage(session, message);
        }
    }

    private void logSessionConnected() {
        System.out.println("새로운 사용자 접속");
        System.out.println("session 안의 요소 개수: " + sessions.size());
        for (WebSocketSession webSocketSession : sessions) {
            System.out.println(webSocketSession.getId());
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
