package LuckyVicky.backend.global.api_payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode {
    // Common
    OK(HttpStatus.OK, "COMMON_200", "Success"),
    CREATED(HttpStatus.CREATED, "COMMON_201", "Created"),

    // User
    USER_LOGIN_SUCCESS(HttpStatus.CREATED, "USER_2011", "회원가입& 로그인이 완료되었습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER_2001", "로그아웃 되었습니다."),
    USER_REISSUE_SUCCESS(HttpStatus.OK, "USER_2002", "토큰 재발급이 완료되었습니다."),
    USER_DELETE_SUCCESS(HttpStatus.OK, "USER_2003", "회원탈퇴가 완료되었습니다."),
    USER_NICKNAME_SUCCESS(HttpStatus.OK, "USER_2004", "닉네임 생성/수정이 완료되었습니다."),
    USER_INFO_UPDATE_SUCCESS(HttpStatus.OK, "USER_2005", "회원 정보 수정이 완료 되었습니다."),
    USER_INFO_VIEW_SUCCESS(HttpStatus.OK, "USER_2006", "회원 정보 조회가 완료 되었습니다."),
    USER_PROFILE_IMAGE_UPDATE_SUCCESS(HttpStatus.OK, "USER_2007", "프로필 사진 이미지 업로드가 완료 되었습니다."),
    USER_DELIVERY_INFORMATION_UPDATE_SUCCESS(HttpStatus.OK, "USER_2008", "배송지 정보가 저장 완료되었습니다."),
    USER_DELIVERY_INFORMATION_VIEW_SUCCESS(HttpStatus.OK, "USER_2009", "배송지 정보 조회가 완료되었습니다."),
    USER_MYPAGE_VIEW_SUCCESS(HttpStatus.OK, "USER_2010", "마이페이지 정보 조회가 완료되었습니다."),

    // Item (상품 관련 성공 코드)
    ITEM_CREATE_SUCCESS(HttpStatus.CREATED, "ITEM_2011", "상품 생성이 완료되었습니다."),
    ITEM_UPDATE_SUCCESS(HttpStatus.OK, "ITEM_2008", "상품 수정이 완료되었습니다."),
    ITEM_DELETE_SUCCESS(HttpStatus.OK, "ITEM_2009", "상품 삭제가 완료되었습니다."),
    ITEM_VIEW_SUCCESS(HttpStatus.OK, "ITEM_2010", "상품 조회가 완료되었습니다."),
    ITEM_IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "ITEM_2012", "상품 이미지 업로드가 완료되었습니다."),
    ITEM_LIKE_SUCCESS(HttpStatus.OK, "ITEM_2001", "상품 좋아요가 완료되었습니다."),
    ITEM_UNLIKE_SUCCESS(HttpStatus.OK, "ITEM_2002", "상품 좋아요가 취소되었습니다."),
    ITEM_ENHANCE_SUCCESS(HttpStatus.OK, "ITEM_2003", "상품 강화 화면에 필요한 요소들이 반환 완료되었습니다."),
    ITEM_DESCRIPTION_VIEW_SUCCESS(HttpStatus.OK, "ITEM_2013", "상품 상세 정보 반환이 완료되었습니다."),

    // Pachinko
    PACHINKO_GET_SQUARES_SUCCESS(HttpStatus.OK, "PACHINKO_2001", "빠칭코 선택 완료된 칸들을 반환 완료되었습니다."),
    PACHINKO_START_SUCCESS(HttpStatus.OK, "PACHINKO_2002", "빠칭코 첫 게임의 각 칸에 대한 보상이 정해졌습니다."),
    PACHINKO_REWARD_SHOW_SUCCESS(HttpStatus.OK, "PACHINKO_2003", "빠칭코 게임 종료 후, 각 사용자가 받는 보상을 반환 완료했습니다."),

    // Invitation
    INVITE_ACCEPT_SUCCESS(HttpStatus.OK, "INVITE_2011", "초대 수락이 완료되었습니다."),

    // Enhance
    ENHANCE_LIST_SUCCESS(HttpStatus.OK, "ENHANCE_2001", "상품 강화 화면에 필요한 요소들이 반환 완료되었습니다."),
    ENHANCE_RESULT_SUCCESS(HttpStatus.OK, "ENHANCE_2002", "강화 시도 후, 결과 반환이 완료되었습니다"),

    // Roulette
    ROULETTE_SUCCESS(HttpStatus.OK, "ROULETTE_2001", "룰렛 보상이 저장 되었습니다"),
    ROULETTE_AVAILABILITY_SUCCESS(HttpStatus.OK, "ROULETTE_2002", "룰렛 돌리기가 가능합니다"),

    // Attendance
    ATTENDANCE_SUCCESS(HttpStatus.OK, "ATTENDANCE_2001", "출석 처리가 완료되었습니다."),
    // Ranking
    RANKING_CURRENT_WEEK_SUCCESS(HttpStatus.OK, "RANKING_2001", "현재 주차 상품 별 랭킹을 반환이 완료되었습니다."),
    RANKING_PREVIOUS_WEEK_SUCCESS(HttpStatus.OK, "RANKING_2002", "이전 주차 상품 별 랭킹을 반환이 완료되었습니다."),
    RANKING_NEXT_WEEK_SUCCESS(HttpStatus.OK, "RANKING_2003", "다음 주차 상품 별 랭킹을 반환이 완료되었습니다."),

    // Sms
    SMS_CERTIFICATE_SEND_SUCCESS(HttpStatus.OK, "SMS_2001", "인증 메시지 전송이 완료되었습니다."),
    SMS_CERTIFICATE_SUCCESS(HttpStatus.OK, "SMS_2002", "문자 인증이 완료되었습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(true)
                .code(this.code)
                .message(this.message)
                .build();
    }
}
