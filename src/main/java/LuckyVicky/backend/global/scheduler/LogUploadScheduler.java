package LuckyVicky.backend.global.scheduler;

import LuckyVicky.backend.global.s3.S3LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogUploadScheduler {

    private final S3LogService logService;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void uploadDailyLogToS3() {
        log.error("Uploading Log to S3 with Scheduler");
        logService.uploadOrAppendLog("yesterday", true);
        log.error("Uploaded Log to S3 with Scheduler");
    }
}