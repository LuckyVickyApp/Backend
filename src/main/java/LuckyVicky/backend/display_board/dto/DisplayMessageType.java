package LuckyVicky.backend.display_board.dto;

import java.time.Duration;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public enum DisplayMessageType {
    PACHINKO_S_JEWEL_MESSAGE(Duration.ofMinutes(10), "님이 파칭코에서 S급 보석을 획득하셨습니다.");

    private final Duration duration;
    private final String messageForm;

    DisplayMessageType(Duration duration, String messageForm) {
        this.duration = duration;
        this.messageForm = messageForm;
    }
}
