package LuckyVicky.backend.display_board.service;

import LuckyVicky.backend.display_board.converter.DisplayBoardConverter;
import LuckyVicky.backend.display_board.domain.DisplayMessage;
import LuckyVicky.backend.display_board.domain.DisplayMessageType;
import LuckyVicky.backend.display_board.repository.DisplayMessageRepository;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.user.domain.User;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisplayBoardService {
    private static final String INITIALIZE_QUEUE_SUCCESS_MESSAGE = "Successfully initialized queue with {} messages.";
    private static final String INITIALIZE_QUEUE_FAIL_MESSAGE = "Failed to initialize the display message queue: {}";

    private final DisplayMessageRepository displayMessageRepository;
    private final Queue<DisplayMessage> displayMessageQueue = new LinkedList<>();

    @PostConstruct
    private void initializeQueue() {
        try {
            displayMessageQueue.clear();

            LocalDateTime now = LocalDateTime.now();
            List<DisplayMessage> messages = displayMessageRepository.findActiveDisplayMessages(now);

            displayMessageQueue.addAll(messages);

            log.info(INITIALIZE_QUEUE_SUCCESS_MESSAGE,displayMessageQueue.size());

        } catch (Exception e) {
            log.error(INITIALIZE_QUEUE_FAIL_MESSAGE, e.getMessage(), e);
        }
    }

    public DisplayMessage getNextDisplayMessage() {
        LocalDateTime now = LocalDateTime.now();

        System.out.println(displayMessageQueue);

        while(!displayMessageQueue.isEmpty()) {
            // pop
            DisplayMessage message =  displayMessageQueue.poll();

            // displayStartTime <= now <= displayEndTime
            if (!message.getDisplayStartTime().isAfter(now) && !message.getDisplayEndTime().isBefore(now)) {
                displayMessageQueue.offer(message);
                return message;
            }
        }

        return null;
    }

    private void addDisplayMessage(DisplayMessageType displayMessageType, String content) {
        try {
            DisplayMessage displayMessage = DisplayBoardConverter.saveDisplayMessage(displayMessageType, content);
            displayMessageRepository.save(displayMessage);

            displayMessageQueue.offer(displayMessage);

            // System.out.println("Successfully added DisplayMessage: " + displayMessage);
        } catch (Exception e) {
            // System.out.println("Error adding DisplayMessage for content: " +  content + ", type: " + displayMessageType);
            throw e; // 재처리를 위해 예외를 다시 던질 수 있음
        }
    }

    public void addPachinkoSJewelMessage(User user) {
        String content = user.getNickname() + DisplayMessageType.PACHINKO_S_JEWEL_MESSAGE.getMessageFormList().get(0);
        addDisplayMessage(DisplayMessageType.PACHINKO_S_JEWEL_MESSAGE, content);
    }

    public void addEnhanceItem1stMessage(User user, Item item, Integer ranking) {
        if (ranking != 1) return;

        List<String> messageFormList = DisplayMessageType.ENHANCE_ITEM_1ST_MESSAGE.getMessageFormList();

        String content = user.getNickname() + messageFormList.get(0) + item.getName() + messageFormList.get(1);

        addDisplayMessage(DisplayMessageType.ENHANCE_ITEM_1ST_MESSAGE, content);
    }
}
