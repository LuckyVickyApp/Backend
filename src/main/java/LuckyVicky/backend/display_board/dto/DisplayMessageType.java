package LuckyVicky.backend.display_board.dto;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public enum DisplayMessageType {
    PACHINKO_S_JEWEL_MESSAGE(Duration.ofMinutes(10),  List.of("님이 파칭코에서 S급 보석을 획득하셨습니다!")),
    ENHANCE_ITEM_1ST_MESSAGE(Duration.ofMinutes(10), List.of("님이 ", "에서 1등을 차지하셨습니다!"));

    private final Duration duration;
    private final List<String> messageFormList;

    DisplayMessageType(Duration duration, List<String> messageFormList) {
        this.duration = duration;
        this.messageFormList = messageFormList;
    }
}
