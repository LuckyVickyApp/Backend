package LuckyVicky.backend.global.fcm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FcmRequestDto {

    @Schema(description = "FcmSimpleReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FcmSimpleReqDto {
        @Schema(description = "알림 대상인 디바이스의 토큰")
        private String deviceToken;

        @Schema(description = "알림 제목")
        private String title;

        @Schema(description = "알림 내용")
        private String body;
    }

}