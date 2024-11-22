package LuckyVicky.backend.attendance.dto;

import LuckyVicky.backend.enhance.domain.JewelType;
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
        @Schema(description = "출석 일차", example = "1")
        private int day;

        @Schema(description = "보석 종류", example = "B")
        private JewelType jewelType;

        @Schema(description = "보석 개수", example = "1")
        private int jewelCount;
    }
}
