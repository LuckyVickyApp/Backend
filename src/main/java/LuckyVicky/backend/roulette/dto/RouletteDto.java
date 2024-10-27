package LuckyVicky.backend.roulette.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RouletteDto {

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
}
