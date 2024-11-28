package LuckyVicky.backend.display_board.converter;

import LuckyVicky.backend.display_board.domain.DisplayMessage;
import LuckyVicky.backend.display_board.domain.DisplayMessageType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

public class DisplayBoardConverter {
    public static DisplayMessage saveDisplayMessage(DisplayMessageType displayMessageType, String content) {

        return DisplayMessage.builder()
                .displayMessageType(displayMessageType)
                .displayStartTime(LocalDateTime.now())
                .displayEndTime(LocalDateTime.now().plus(displayMessageType.getDuration()))
                .content(content)
                .build();
    }
}
