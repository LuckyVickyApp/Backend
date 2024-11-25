package LuckyVicky.backend.display_board.service;

import LuckyVicky.backend.display_board.dto.DisplayMessage;
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
}
