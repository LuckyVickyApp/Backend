package LuckyVicky.backend.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        File localFile = getLocalLogFile(day);
        if (!localFile.exists()) {
            log.warn("No local log file found for day={}", day);
            return;
        }

        String s3Key = buildS3Key(day);
        File tempFile = new File("temp-" + localFile.getName());

        try {
            // S3에 기존 로그 파일이 있는 경우 다운로드하여 병합
            if (amazonS3.doesObjectExist(bucket, s3Key)) {
                log.info("Existing log found in S3. Merging logs...");
                mergeLogsFromS3(localFile, tempFile, s3Key);
            } else {
                Files.copy(localFile.toPath(), tempFile.toPath());
            }

            // 병합된 로그 파일을 S3에 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, s3Key, tempFile));
            log.info("Uploaded merged log to S3: {} -> s3://{}/{}", tempFile.getName(), bucket, s3Key);

            // 업로드 후 로컬 로그 파일 삭제 및 Logback 초기화
            Files.deleteIfExists(localFile.toPath());
            log.info("Deleted local log file: {}", localFile.getName());

        } catch (Exception e) {
            log.error("Failed to upload or merge log file to S3. day={}, file={}, key={}", day, localFile, s3Key, e);
        } finally {
            tempFile.delete(); // 임시 파일 삭제
        }
    }

    private void mergeLogsFromS3(File localFile, File tempFile, String s3Key) throws IOException {
        try (S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, s3Key));
             InputStream s3InputStream = s3Object.getObjectContent();
             OutputStream tempOutputStream = new FileOutputStream(tempFile, true);
             InputStream localInputStream = new FileInputStream(localFile)) {

            // S3 로그 내용을 임시 파일에 복사
            copyContent(s3InputStream, tempOutputStream);

            // 로컬 로그 내용을 임시 파일에 이어서 복사
            copyContent(localInputStream, tempOutputStream);
        }
    }

    private void copyContent(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) > 0) {
            output.write(buffer, 0, bytesRead);
        }
    }

    private File getLocalLogFile(String day) {
        String logHome = System.getProperty("LOG_HOME", "./logs");
        LocalDate targetDate = "yesterday".equals(day) ? LocalDate.now().minusDays(1) : LocalDate.now();
        String dateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if ("yesterday".equals(day)) {
            return new File(logHome, "error-" + dateStr + ".log");
        } else {
            return new File(logHome, "error.log");
        }
    }

    private String buildS3Key(String day) {
        LocalDate targetDate = "yesterday".equals(day) ? LocalDate.now().minusDays(1) : LocalDate.now();
        String dateStr = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String activeProfile = environment.getProperty("spring.profiles.active", "default");

        return String.format("logs/%s/%s-error-%s.log", activeProfile, activeProfile, dateStr);
    }
}
