package LuckyVicky.backend.pachinko.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PachinkoResponseDto {
    @Schema(description = "PachinkoRewardResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PachinkoRewardResDto {
        @Schema(description = "S급 보석")
        private Long jewelS;

        @Schema(description = "A급 보석")
        private Long jewelA;

        @Schema(description = "B급 보석")
        private Long jewelB;
    }

}
