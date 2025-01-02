package LuckyVicky.backend.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3LogService {

    private final AmazonS3 amazonS3;
    private final Environment environment;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void uploadDailyLog(String day) {
        File targetFile = getLocalLogFile(day);

        if (!targetFile.exists()) {
            log.warn("No local log file found for day={}", day);
            return;
        }

        String s3Key = buildS3Key(day);

        try {
            // S3에 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, s3Key, targetFile));
            log.info("Uploaded log to S3: {} -> s3://{}/{}", targetFile.getName(), bucket, s3Key);

            // 업로드 후 로컬 파일 삭제
            Files.deleteIfExists(targetFile.toPath());

        } catch (Exception e) {
            log.error("Failed to upload log file to S3. day={}, file={}, key={}", day, targetFile, s3Key, e);
        }
    }

    private File getLocalLogFile(String day) {
        // LOG_HOME=/var/logs/myapp
        String logHome = System.getProperty("LOG_HOME", "./logs");
        LocalDate targetDate = "yesterday".equals(day) ? LocalDate.now().minusDays(1) : LocalDate.now();
        String dateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if ("yesterday".equals(day)) {
            // 롤오버된 파일: /var/logs/myapp/error-2025-01-01.log
            return new File(logHome, "error-" + dateStr + ".log");
        } else {
            // 아직 열려 있는 로그: /var/logs/myapp/error.log
            return new File(logHome, "error.log");
        }
    }

    private String buildS3Key(String day) {
        // 원하는 규칙대로 S3에 업로드될 파일 키 생성
        LocalDate targetDate = "yesterday".equals(day) ? LocalDate.now().minusDays(1) : LocalDate.now();
        String dateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String activeProfile = environment.getProperty("spring.profiles.active", "default");

        return String.format("logs/" + activeProfile + "/" + activeProfile + "-error-%s.log", dateStr);
    }
}
