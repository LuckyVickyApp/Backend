package LuckyVicky.backend.global.s3;

import static LuckyVicky.backend.global.util.Constant.LOG_LOCAL_ERROR_FILE_NAME;
import static LuckyVicky.backend.global.util.Constant.LOG_LOCAL_FILE_DIRECTORY;
import static LuckyVicky.backend.global.util.Constant.LOG_LOGBACK_ERROR_FILE_NAME;
import static LuckyVicky.backend.global.util.Constant.LOG_LOGBACK_FILE_DIRECTORY;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogFileManager {

    public File getLogFile() {
        return new File(LOG_LOCAL_FILE_DIRECTORY + LOG_LOCAL_ERROR_FILE_NAME);
    }

    public void recreateLogFile(boolean releaseLogContext) {
        File logFile = getLogFile();

        if (releaseLogContext) {
            releaseLogFile(); // API나 스케줄러 호출 시 잠금 해제
        }

        deleteLogFile(logFile);

        resetLogbackContext();

        log.error("This is a dummy log to recreate the log file.");
    }

    public LoggerContext releaseLogFile() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.stop(); // Logback 파일 핸들러 잠금 해제
        return loggerContext;
    }

    public void resetLogbackContext() {
        LoggerContext loggerContext = releaseLogFile();

        try {
            loggerContext.reset();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            configurator.doConfigure(LOG_LOGBACK_FILE_DIRECTORY + LOG_LOGBACK_ERROR_FILE_NAME);
            loggerContext.start();
            log.error("Logback context reset. Log file recreated.");
        } catch (JoranException e) {
            log.error("Failed to reset Logback context", e);
        }
    }

    public void deleteLogFile(File file) {
        if (!file.delete()) {
            file.deleteOnExit(); // 삭제 실패 시 JVM 종료 시 삭제 예약
            log.warn("Marked log file for deletion on JVM exit: {}", file.getAbsolutePath());
        } else {
            log.warn("Already deleted OR Log file does not exist");
        }
    }
}