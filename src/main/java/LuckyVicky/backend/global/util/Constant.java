package LuckyVicky.backend.global.util;

import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constant {

    @Value("${fcm.project-id}")
    private String fcmProjectId;

    // Log
    public static final String LOG_S3_DIRECTORY = "logs/";
    public static final String LOG_LOCAL_FILE_DIRECTORY = "src/main/resources/logs/";
    public static final String LOG_LOCAL_ERROR_FILE_NAME = "error.log";
    public static final String LOG_LOGBACK_FILE_DIRECTORY = "src/main/resources/";
    public static final String LOG_LOGBACK_ERROR_FILE_NAME = "logback-spring.xml";

    public static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Phone
    public static final String PHONE_NUMBER_PATTERN = "^\\d{11}$";
    public static final String AES_PHONE_NUMBER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    // Attendance
    public static final int ATTENDANCE_CYCLE_DAYS = 12;

    // Fcm
    public static final int DEVICE_REGISTRATION_LIMIT = 3;
    public static final String FIREBASE_SECRET_KEY_PATH = "firebase/serviceAccountKey.json";

    // Notice
    public static final String CONVERTER_INSTANTIATION_NOT_ALLOWED = "Converter class는 인스턴스화가 불가능합니다.";

    // FCM
    public static final String FCM_TITLE_CURRENT_WEEK_AWARD = "이번 주 강화 보상";


    // FCM API URL 동적으로 생성
    public String getFcmApiUrl() {
        return "https://fcm.googleapis.com/v1/projects/" + fcmProjectId + "/messages:send";
    }
}
