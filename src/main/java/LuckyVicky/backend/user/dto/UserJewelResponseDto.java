package LuckyVicky.backend.user.dto;

import LuckyVicky.backend.enhance.domain.JewelType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserJewelResponseDto {

    @Schema(description = "UserJewelResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserJewelResDto {
        @Schema(description = "보석 종류")
        private JewelType jewelType;

        @Schema(description = "보유 개수")
        private Integer count;
    }
}
