package LuckyVicky.backend.global.s3;

import static LuckyVicky.backend.global.util.Constant.LOG_DATE_FORMAT;
import static LuckyVicky.backend.global.util.Constant.LOG_LOGBACK_ERROR_FILE_NAME;
import static LuckyVicky.backend.global.util.Constant.LOG_LOGBACK_FILE_DIRECTORY;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
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
        log.info("Starting uploadDailyLog for day: {}", day);
        File localFile = getLocalLogFile(day);
        ensureLogFileExists(localFile); // 로그 파일이 없으면 생성

        String s3Key = buildS3Key(day);
        File tempFile = new File("temp-" + localFile.getName());

        try {
            // S3에 기존 로그 파일이 있는 경우 다운로드하여 병합
            if (amazonS3.doesObjectExist(bucket, s3Key)) {
                log.info("Existing log found in S3. Merging logs for key: {}", s3Key);
                mergeLogsFromS3(localFile, tempFile, s3Key);
            } else {
                log.info("No existing log found in S3 for key: {}. Copying local file.", s3Key);
                Files.copy(localFile.toPath(), tempFile.toPath());
            }

            // 병합된 로그 파일을 S3에 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, s3Key, tempFile));
            log.info("Uploaded merged log to S3: {} -> s3://{}/{}", tempFile.getName(), bucket, s3Key);

            // 업로드 후 로컬 로그 파일 삭제 및 Logback 초기화
            Files.deleteIfExists(localFile.toPath());
            log.info("Deleted local log file: {}", localFile.getName());
            resetLogbackContext();

        } catch (Exception e) {
            log.error("Failed to upload or merge log file to S3. day={}, file={}, key={}", day, localFile, s3Key, e);
        } finally {
            if (tempFile.delete()) {
                log.info("Temporary file deleted: {}", tempFile.getName());
            } else {
                log.warn("Failed to delete temporary file: {}", tempFile.getName());
            }
        }
    }

    private void resetLogbackContext() {
        log.info("Resetting Logback context to create a new log file.");
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.stop(); // 기존 컨텍스트 정지

        try {
            loggerContext.reset(); // 컨텍스트 초기화
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            configurator.doConfigure(LOG_LOGBACK_FILE_DIRECTORY + LOG_LOGBACK_ERROR_FILE_NAME);
            loggerContext.start(); // 컨텍스트 다시 시작
            log.info("Logback context has been reset. New log file will be created.");
        } catch (JoranException e) {
            log.error("Failed to reset Logback context", e);
        }

        ensureLogFileExists(getLocalLogFile("today"));
        log.info("Dummy log written to trigger new file creation.");
    }

    private void mergeLogsFromS3(File localFile, File tempFile, String s3Key) throws IOException {
        log.info("Merging S3 log and local log for key: {}", s3Key);
        try (S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, s3Key));
             InputStream s3InputStream = s3Object.getObjectContent();
             OutputStream tempOutputStream = new FileOutputStream(tempFile, true);
             InputStream localInputStream = new FileInputStream(localFile)) {

            // S3 로그 내용을 임시 파일에 복사
            log.info("Copying S3 log content to temp file.");
            copyContent(s3InputStream, tempOutputStream);

            // 로컬 로그 내용을 임시 파일에 이어서 복사
            log.info("Appending local log content to temp file.");
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

    private void ensureLogFileExists(File logFile) {
        if (!logFile.exists()) {
            try {
                Files.createFile(logFile.toPath());
                log.info("Log file manually created: {}", logFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("Failed to create log file manually: {}", logFile.getAbsolutePath(), e);
            }
        } else {
            log.info("Log file already exists: {}", logFile.getAbsolutePath());
        }
    }

    private File getLocalLogFile(String day) {
        String logHome = System.getProperty("LOG_HOME", "./logs");
        LocalDate targetDate = "yesterday".equals(day) ? LocalDate.now().minusDays(1) : LocalDate.now();
        String dateStr = targetDate.format(LOG_DATE_FORMAT);

        if ("yesterday".equals(day)) {
            return new File(logHome, "error-" + dateStr + ".log");
        } else {
            return new File(logHome, "error.log");
        }
    }

    private String buildS3Key(String day) {
        LocalDate targetDate = "yesterday".equals(day) ? LocalDate.now().minusDays(1) : LocalDate.now();
        String dateStr = targetDate.format(LOG_DATE_FORMAT);

        String activeProfile = environment.getProperty("spring.profiles.active", "default");

        return String.format("logs/%s/%s-error-%s.log", activeProfile, activeProfile, dateStr);
    }
}
