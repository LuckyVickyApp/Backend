package LuckyVicky.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class UserRequestDto {
    @Schema(description = "UserReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserReqDto {

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "이메일id{social}")
        private String username;

        @Schema(description = "social type")
        private String provider;

        @Schema(description = "device token")
        private String deviceToken;

    }

    @Schema(description = "UserNicknameReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserNicknameReqDto {

        @Schema(description = "닉네임")
        private String nickname;

    }

    @Schema(description = "UserAddressReqDto")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserAddressReqDto {
        @Schema(description = "수령자 이름")
        private String recipientName;

        @Schema(description = "전화번호")
        private String phoneNumber;

        @Schema(description = "도로명 주소")
        private String streetAddress;

        @Schema(description = "상세 주소")
        private String detailedAddress;
    }
}
