package LuckyVicky.backend.global.util;

import java.time.format.DateTimeFormatter;

public class Constant {
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

    // Notice
    public static final String CONVERTER_INSTANTIATION_NOT_ALLOWED = "Converter class는 인스턴스화가 불가능합니다.";

}
