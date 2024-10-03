package LuckyVicky.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
