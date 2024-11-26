package LuckyVicky.backend.display_board.service;

import LuckyVicky.backend.display_board.converter.DisplayBoardConverter;
import LuckyVicky.backend.display_board.dto.DisplayMessage;
import LuckyVicky.backend.display_board.dto.DisplayMessageType;
import LuckyVicky.backend.display_board.repository.DisplayMessageRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
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

    public void addDisplayMessage(String nickname, DisplayMessageType displayMessageType) {
        try {
            DisplayMessage displayMessage = DisplayBoardConverter.saveDisplayMessage(displayMessageType, nickname);
            displayMessageRepository.save(displayMessage);
            System.out.println("Successfully added DisplayMessage: {}" + displayMessage);
        } catch (Exception e) {
            System.out.println("Error adding DisplayMessage for nickname: " +  nickname + ", type: " + displayMessageType);
            throw e; // 재처리를 위해 예외를 다시 던질 수 있음
        }
    }
}
