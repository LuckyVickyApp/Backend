package LuckyVicky.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
public class UserResponseDto {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyPageUserDto {
        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "주소")
        private String address;

        @Schema(description = "프로필 이미지")
        private String profileImage;

        @Schema(description = "가입 날짜")
        private String signInDate;

        @Schema(description = "내 코드")
        private String inviteCode;

    }

}

