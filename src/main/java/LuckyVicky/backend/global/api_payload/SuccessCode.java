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

    // Item (상품 관련 성공 코드)
    ITEM_CREATE_SUCCESS(HttpStatus.CREATED, "ITEM_2011", "상품 생성이 완료되었습니다."),
    ITEM_UPDATE_SUCCESS(HttpStatus.OK, "ITEM_2008", "상품 수정이 완료되었습니다."),
    ITEM_DELETE_SUCCESS(HttpStatus.OK, "ITEM_2009", "상품 삭제가 완료되었습니다."),
    ITEM_VIEW_SUCCESS(HttpStatus.OK, "ITEM_2010", "상품 조회가 완료되었습니다."),
    ITEM_IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "ITEM_2012", "상품 이미지 업로드가 완료되었습니다."),
    ITEM_LIKE_SUCCESS(HttpStatus.OK, "ITEM_2001", "상품 좋아요가 완료되었습니다."),
    ITEM_UNLIKE_SUCCESS(HttpStatus.OK, "ITEM_2002", "상품 좋아요가 취소되었습니다."),
    ITEM_ENHANCE_SUCCESS(HttpStatus.OK, "ITEM_2003", "상품 강화 화면에 필요한 요소들이 반환 완료되었습니다."),

    // Invitation
    INVITE_ACCEPT_SUCCESS(HttpStatus.OK, "INVITE_2011", "초대 수락이 완료되었습니다."),

    ;


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
