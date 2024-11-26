package LuckyVicky.backend.display_board.converter;

import LuckyVicky.backend.display_board.dto.DisplayMessage;
import LuckyVicky.backend.display_board.dto.DisplayMessageType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DisplayBoardConverter {
    public static DisplayMessage saveDisplayMessage(DisplayMessageType displayMessageType, String nickname) {

        return DisplayMessage.builder()
                .displayMessageType(displayMessageType)
                .displayStartTime(LocalDateTime.now())
                .displayEndTime(LocalDateTime.now().plus(displayMessageType.getDuration()))
                .content(nickname + displayMessageType.getMessageForm())
                .build();
    }
}
