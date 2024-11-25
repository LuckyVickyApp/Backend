package LuckyVicky.backend.global.s3;

import static LuckyVicky.backend.global.util.Constant.LOG_DATE_FORMAT;
import static LuckyVicky.backend.global.util.Constant.LOG_S3_DIRECTORY;

import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3LogService {

    private final AmazonS3 amazonS3;
    private final Environment environment;
    private final LogFileManager logFileManager;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void uploadOrAppendLog(String day, boolean releaseLogContext) {
        File localFile = logFileManager.getLogFile();

        if (!localFile.exists()) {
            logFileManager.recreateLogFile(releaseLogContext);
            throw new GeneralException(ErrorCode.ERROR_LOG_NOT_FOUND);
        }

        String s3Key = getS3Key(day);

        try {
            File tempFile = File.createTempFile("s3-log-", ".log");

            mergeFilesFromS3(tempFile, s3Key, localFile);

            uploadLargeFileToS3(tempFile, s3Key);

            logFileManager.recreateLogFile(releaseLogContext);

        } catch (Exception e) {
            // 추가적인 오류 처리 및 복구 로직
            log.error("Failed to upload or append log to S3: {}", s3Key, e);
        }
    }

    public void uploadLargeFileToS3(File file, String s3Key) {
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .build();

        try {
            Upload upload = transferManager.upload(bucket, s3Key, file);
            log.info("Multipart upload started for: {}", s3Key);

            upload.waitForCompletion();
            log.info("Multipart upload completed for: {}", s3Key);
        } catch (Exception e) {
            log.error("Multipart upload failed for: {}", s3Key, e);
        } finally {
            transferManager.shutdownNow();
        }
    }

    private String getS3Key(String day) {
        String activeProfile = environment.getProperty("spring.profiles.active", "default");
        LocalDate date;

        if ("today".equals(day)) {
            date = LocalDate.now();
        } else if ("yesterday".equals(day)) {
            date = LocalDate.now().minusDays(1);
        } else {
            throw new GeneralException(ErrorCode.INVALID_PARAMETER);
        }

        String formattedDate = date.format(LOG_DATE_FORMAT);
        return LOG_S3_DIRECTORY + activeProfile + "/" + activeProfile + "-error-" + formattedDate + ".log";
    }


    private void mergeFilesFromS3(File tempFile, String s3Key, File localFile) throws IOException {
        if (amazonS3.doesObjectExist(bucket, s3Key)) {
            appendS3ContentToTempFile(tempFile, s3Key);
        }
        appendLocalContentToTempFile(tempFile, localFile);
    }

    private void appendS3ContentToTempFile(File tempFile, String s3Key) throws IOException {

        try (S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, s3Key));
             InputStream s3InputStream = s3Object.getObjectContent();
             OutputStream tempOutputStream = new FileOutputStream(tempFile)) {

            copyContent(s3InputStream, tempOutputStream);
            log.info("Merged S3 log content: {}", s3Key);
        }
    }

    private void appendLocalContentToTempFile(File tempFile, File localFile) throws IOException {
        try (InputStream localInputStream = new FileInputStream(localFile);
             OutputStream tempOutputStream = new FileOutputStream(tempFile, true)) {

            copyContent(localInputStream, tempOutputStream);
            log.info("Merged local log content: {}", localFile.getName());
        }
    }

    private void copyContent(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) > 0) {
            output.write(buffer, 0, bytesRead);
        }
    }
}