package LuckyVicky.backend.roulette.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RouletteResponseDto {

    @Schema(description = "룰렛 결과 DTO")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RouletteResultDto {
        @Schema(description = "룰렛 결과 메시지", example = "B급 보석 1개")
        private String message;

        @Schema(description = "받은 보석 개수", example = "1")
        private int jewelCount;
    }

    @Schema(description = "룰렛 가능 여부 DTO")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RouletteAvailableDto {
        @Schema(description = "룰렛을 돌릴 수 있는지 여부", example = "true")
        private boolean available;

        @Schema(description = "남은 대기 시간 (초 단위)", example = "600")
        private int remainingTime;
    }
}
