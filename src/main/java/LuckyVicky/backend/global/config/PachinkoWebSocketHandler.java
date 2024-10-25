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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PachinkoWebSocketHandler extends TextWebSocketHandler {

    private final PachinkoService pachinkoService;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    private final ObjectMapper objectMapper = new ObjectMapper();
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
                session.sendMessage(new TextMessage("JWT 검증 실패하여 연결 종료합니다."));
                session.close();
                return;
            }
        }

        // 칸 선택 처리 (JWT 검증이 완료된 후)
        if (node.has("square")) {
            int selectedSquare = node.get("square").asInt();
            User user = (User) session.getAttributes().get("user");

            if (user != null) {
                long currentRound = pachinkoService.getCurrentRound();

                if(!pachinkoService.noMoreJewel(user)) {
                    if(pachinkoService.canSelectMore(user, currentRound)) {
                        if (pachinkoService.selectSquare(user, currentRound, selectedSquare)) {
                            for (WebSocketSession webSocketSession : sessions) {
                                webSocketSession.sendMessage(new TextMessage(user.getUsername() + "가 " + selectedSquare + "을 선택했습니다."));
                            }
                            checkGameStatusAndCloseSessionsIfNeeded();
                        } else {
                            session.sendMessage(new TextMessage( selectedSquare + "번째 칸은 이미 다른 사용자에 의해 선택되었습니다."));
                        }
                    } else {
                        session.sendMessage(new TextMessage("이미 3칸을 선택하셔서 더 이상 칸을 선택할 수 없습니다."));
                    }
                } else {
                    session.sendMessage(new TextMessage("칸을 선택할때 필요한 B급 보석이 부족합니다."));
                }

            } else {
                session.sendMessage(new TextMessage("유저가 인증되지 않았습니다."));
            }
        } else {
            session.sendMessage(new TextMessage("Invalid message format: 'square' 필드에 값이 없습니다."));
        }
    }

    private void checkGameStatusAndCloseSessionsIfNeeded() {
        if (pachinkoService.isGameOver()) {
            System.out.println("게임 끝남 확인. 보상 전달 시작");
            broadcastMessage("해당 판이 종료되었습니다. 10초 후 새로운 판이 시작됩니다. ");
            pachinkoService.giveRewards();
            broadcastMessage("보상 전달이 완료되었습니다. ");

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
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("해당 세션 제거");
        sessions.remove(session);
    }


    public void endGameForAll() {
        List<WebSocketSession> sessionsCopy = new ArrayList<>(sessions);
        for (WebSocketSession session : sessionsCopy) {
            try {
                session.sendMessage(new TextMessage("해당 게임의 세션을 모두 종료합니다."));
                session.close(CloseStatus.NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sessions.clear();
    }

}
