package LuckyVicky.backend.global.util;

import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constant {

    @Value("${fcm.project-id}")
    private String fcmProjectId;

    // Log
    public static final String LOG_LOGBACK_FILE_DIRECTORY = "src/main/resources/logback-spring.xml";
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
    public static final String FCM_CONGRATULATION = "축하드립니다!!";
    public static final String FCM_CURRENT_WEEK_AWARD_TITLE = "이번 주 강화 보상";
    public static final String FCM_INVITATION_TITLE = "초대 보상으로 보석 지급 완료!!";
    public static final String FCM_PACHINKO_GAME_FINISH_TITLE = "빠칭코 게임이 끝났습니다!";
    public static final String FCM_PACHINKO_GAME_FINISH_BODY = "당신이 뽑은 칸의 결과는?!? 빠칭코 게임 화면에서 확인해보세요!!";
    public static final String FCM_ROULETTE_CAN_START_TITLE = "룰렛 돌리러 고고씽!!";
    public static final String FCM_ROULETTE_CAN_START_BODY = "룰렛 대기 시간 10분이 끝났습니다!";

    // FCM API URL 동적으로 생성
    public String getFcmApiUrl() {
        return "https://fcm.googleapis.com/v1/projects/" + fcmProjectId + "/messages:send";
    }
}
