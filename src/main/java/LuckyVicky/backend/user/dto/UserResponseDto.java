package LuckyVicky.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserResponseDto {

    @Schema(description = "UserMyPageResDto")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserMyPageResDto {
        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "프로필 이미지")
        private String profileImage;
    }

    @Schema(description = "UserDeliveryInformationResDto")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDeliveryInformationResDto {
        @Schema(description = "수령자 이름")
        private String recipientName;

        @Schema(description = "전화번호")
        private String phoneNumber;

        @Schema(description = "도로명 주소")
        private String streetAddress;

        @Schema(description = "상세 주소")
        private String detailedAddress;
    }

    @Schema(description = "UserInformationResDto")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInformationResDto {
        @Schema(description = "이메일")
        private String email;

        @Schema(description = "가입 날짜")
        private String signInDate;

        @Schema(description = "내 코드")
        private String inviteCode;
    }
}

