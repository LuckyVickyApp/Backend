package LuckyVicky.backend.attendance.dto;

import LuckyVicky.backend.enhance.domain.JewelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AttendanceResponseDto {

    @Schema(description = "AttendanceRewardResDto")
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

    @Schema(description = "AttendanceRewardResponseDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttendanceRewardResponseDto {
        @Schema(description = "출석 완료 마지막 날짜", example = "B")
        private int lastAttendanceCheckedDay;

        @Schema(description = "출석 일차", example = "1")
        private List<AttendanceRewardResDto> attendanceRewardResDtoList;
    }

}
