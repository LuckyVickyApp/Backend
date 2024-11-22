package LuckyVicky.backend.global.util;

import java.time.format.DateTimeFormatter;

public class Constant {
    // Log
    public static final String LOG_DIRECTORY = "logs/"; // 로그 파일 디렉토리
    public static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Phone
    public static final String PHONE_NUMBER_PATTERN = "^\\d{11}$";
    public static final String AES_PHONE_NUMBER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    // Attendance
    public static final int ATTENDANCE_CYCLE_DAYS = 12;

    // Notice
    public static final String CONVERTER_INSTANTIATION_NOT_ALLOWED = "Converter class는 인스턴스화가 불가능합니다.";

}
