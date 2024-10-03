package LuckyVicky.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserAddressDto {
        @Schema(description = "주소")
        private String address;
    }
}
