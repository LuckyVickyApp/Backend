package LuckyVicky.backend.global.aop;

import static LuckyVicky.backend.global.util.Constant.LOG_DATE_FORMAT;
import static LuckyVicky.backend.global.util.Constant.LOG_DIRECTORY;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogUploadScheduler {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Scheduled(cron = "0 0 0 * * ?")
    public void uploadYesterdayLogToS3() {
        String yesterdayDate = LocalDate.now().minusDays(1).format(LOG_DATE_FORMAT);
        String logFileName = LOG_DIRECTORY + activeProfile + "/" + "error-" + yesterdayDate + ".log";
        File logFile = new File(logFileName);

        if (!logFile.exists()) {
            log.warn("No log file found for date: {}", yesterdayDate);
            return;
        }

        // S3 업로드
        try {
            amazonS3.putObject(new PutObjectRequest(bucket, logFileName, logFile));
            log.info("Uploaded log file to S3: {}", logFileName);

            // S3 업로드 성공 후, 로컬에서 파일 삭제
            if (logFile.delete()) {
                log.info("Deleted log file from local storage: {}", logFileName);
            } else {
                log.warn("Failed to delete log file '{}' from local storage", logFileName);
            }

        } catch (Exception e) {
            log.error("Failed to upload log file to S3: {}", logFileName, e);
        }
    }
}
