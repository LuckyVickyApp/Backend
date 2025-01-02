package LuckyVicky.backend.global.lifecycle;

import LuckyVicky.backend.global.s3.S3LogService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InstanceShutdownHandler {

    private final S3LogService logService;

    @PreDestroy
    public void onShutdown() {
        log.info("Application is shutting down. Uploading today's log to S3...");
        logService.uploadDailyLog("today");
    }
}