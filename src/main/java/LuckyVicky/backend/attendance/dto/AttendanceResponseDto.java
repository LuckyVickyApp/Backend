package LuckyVicky.backend.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AttendanceResponseDto {

    @Schema(description = "출석 보상 결과 DTO")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttendanceRewardResDto {
        @Schema(description = "보상 메시지", example = "B급 보석 1개")
        private String rewardMessage;

        @Schema(description = "획득한 보석 개수", example = "1")
        private int jewelCount;
    }
}
