package LuckyVicky.backend.global.api_payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {
    // Common
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러, 서버 개발자에게 문의하세요."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_4041", "존재하지 않는 회원입니다."),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "USER_4042", "존재하지 않는 회원입니다.-EMAIL"),
    USER_NOT_FOUND_BY_USERNAME(HttpStatus.NOT_FOUND, "USER_4043", "존재하지 않는 회원입니다.-USERNAME"),
    ALREADY_USED_NICKNAME(HttpStatus.FORBIDDEN, "USER_4031", "이미 사용중인 닉네임입니다."),
    USER_ADDRESS_NULL(HttpStatus.BAD_REQUEST, "USER_4001", "주소값이 비었거나 NULL입니다."),

    // User Jewel
    USER_JEWEL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "USER_5001", "회원의 보석함 정보가 DB에서 일부 혹은 전체가 사라졌습니다."),
    USER_JEWEL_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_4041", "회원의 보석함이 없습니다."),

    // Jwt
    WRONG_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "JWT_4041", "일치하는 리프레시 토큰이 없습니다."),
    TOKEN_INVALID(HttpStatus.FORBIDDEN, "JWT_4032", "유효하지 않은 토큰입니다."),
    TOKEN_NO_AUTH(HttpStatus.FORBIDDEN, "JWT_4033", "권한 정보가 없는 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT_4011", "토큰 유효기간이 만료되었습니다."),

    // Item (상품 관련 오류 코드)
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEM_4041", "존재하지 않는 상품입니다."),
    ITEM_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ITEM_4001", "이미 존재하는 상품명입니다."),
    ITEM_QUANTITY_INVALID(HttpStatus.BAD_REQUEST, "ITEM_4002", "유효하지 않은 수량입니다."),
    ITEM_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ITEM_5001", "상품 생성에 실패하였습니다."),
    ITEM_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ITEM_5002", "상품 수정에 실패하였습니다."),
    ITEM_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ITEM_5003", "상품 삭제에 실패하였습니다."),
    ITEM_IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ITEM_5004", "상품 이미지 업로드에 실패하였습니다."),
    ITEM_ALREADY_LIKE(HttpStatus.BAD_REQUEST, "ITEM_4003", "이미 좋아요한 상품입니다."),
    ITEM_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEM_4042", "존재하지 않는 상품 좋아요입니다."),

    // Invitation
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "INVITE_4041", "존재하지 않는 초대 코드입니다."),
    INVITATION_ALREADY_ACCEPTED(HttpStatus.BAD_REQUEST, "INVITE_4001", "초대 수락을 이미 완료한 상태입니다."),

    // Enhance
    ENHANCE_JEWEL_NOT_FOUND(HttpStatus.NOT_FOUND, "ENHANCE_4041", "존재하지 않는 보석 종류입니다."),
    ENHANCE_JEWEL_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "ENHANCE_4001", "강화를 위한 보석 개수가 부족합니다."),
    ENHANCE_RATE_NOT_FOUND(HttpStatus.NOT_FOUND, "ENHANCE_4042", "존재하지 않는 강화 확률 입니다."),

    // Roulette
    ROULETTE_COOLDOWN(HttpStatus.BAD_REQUEST, "ROULETTE_4001", "룰렛은 10분에 한 번만 돌릴 수 있습니다."),

    // Pachinko
    USER_PACHINKO_NOT_FOUND(HttpStatus.NOT_FOUND, "PACHINKO_4041", "유저가 해당 빠칭코 판에 참여한적이 없습니다."),
    PACHINKO_OUT_OF_BOUND(HttpStatus.BAD_REQUEST, "PACHINKO_4001", "빠칭코 칸의 범위(1~36)를 넘어섰습니다."),
    PACHINKO_NO_MORE_CHANCE(HttpStatus.BAD_REQUEST, "PACHINKO_4002", "이미 세칸을 고르셨습니다."),
    PACHINKO_NO_REWARD(HttpStatus.NOT_FOUND, "PACHINKO_4042", "해당 보석 종류에 대한 보상 레코드가 없습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(false)
                .code(this.code)
                .message(this.message)
                .build();
    }
}
