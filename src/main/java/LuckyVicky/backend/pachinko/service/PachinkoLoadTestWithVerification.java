package LuckyVicky.backend.pachinko.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PachinkoLoadTestWithVerification {

    private static final int USERS_PER_SQUARE = 200;
    private static final int TOTAL_SQUARES = 5;
    private static final String TOKEN_URL = "http://localhost:8080/token/generate";
    private static final String WS_URL = "ws://localhost:8080/pachinko";
    private static final String VERIFY_URL = "http://localhost:8080/game/pachinko/selected-squares";

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        CountDownLatch latch = new CountDownLatch(USERS_PER_SQUARE * TOTAL_SQUARES);

        for (int square = 1; square <= TOTAL_SQUARES; square++) {
            final int finalSquare = square;
            for (int i = 0; i < USERS_PER_SQUARE; i++) {
                final int userIndex = (square - 1) * USERS_PER_SQUARE + i;
                executor.submit(() -> {
                    try {
                        String token = getTokenForUser(userIndex);
                        connectAndSendWebSocket(token, finalSquare);
                    } catch (Exception e) {
                        System.err.println("에러: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();
        executor.shutdown();
        System.out.println("make user finish");

        long end = System.nanoTime();

        // 결과 검증
        Thread.sleep(2000); // 데이터 반영 대기
        verifySelectedSquares();

        double elapsedMs = (end - start) / 1_000_000.0;
        System.out.printf("부하 테스트 완료 – 총 소요 시간: %.2fms (%.2f초)\n", elapsedMs, elapsedMs / 1000.0);
    }

    private static String getTokenForUser(int userNum) throws Exception {
        URL url = new URL(TOKEN_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        String payload = String.format("""
                {
                    "email": "testuser%d@example.com",
                    "username": "user%d",
                    "provider": "test",
                    "deviceToken": "test"
                }
                """, userNum, userNum);

        try (OutputStream os = con.getOutputStream()) {
            os.write(payload.getBytes());
        }

        if (con.getResponseCode() == 200) {
            try (Scanner scanner = new Scanner(con.getInputStream()).useDelimiter("\\A")) {
                String response = scanner.hasNext() ? scanner.next() : "";
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response);
                return root.get("result").get("accessToken").asText();

            }
        } else {
            throw new RuntimeException("토큰 요청 실패: " + con.getResponseCode());
        }
    }

    private static void connectAndSendWebSocket(String token, int square) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        Session session = container.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig config) {
                try {
                    String json = String.format("""
                            {
                                "token": "%s",
                                "square": %d
                            }
                            """, token, square);
                    session.getBasicRemote().sendText(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, URI.create(WS_URL));
        Thread.sleep(200); // 메시지 전송 후 약간 대기
        session.close();
    }

    private static void verifySelectedSquares() throws Exception {
        URL url = new URL(VERIFY_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        if (con.getResponseCode() == 200) {
            try (Scanner scanner = new Scanner(con.getInputStream()).useDelimiter("\\A")) {
                String response = scanner.hasNext() ? scanner.next() : "";
                int resultStart = response.indexOf("[");
                int resultEnd = response.indexOf("]", resultStart) + 1;
                String resultJsonArray = response.substring(resultStart, resultEnd);
                String[] squares = resultJsonArray.replaceAll("[\\[\\]\\s]", "").split(",");

                System.out.printf("최종 선택된 칸 개수: %d개%n", squares.length);
                if (squares.length == TOTAL_SQUARES) {
                    System.out.printf("테스트 성공: %d개 칸이 정확히 채워졌습니다.\n", squares.length);
                } else {
                    System.err.println("테스트 실패: 선택된 칸 수 = " + squares.length);
                }
            }
        } else {
            System.err.println(" 확인 API 호출 실패: " + con.getResponseCode());
        }
    }
}

