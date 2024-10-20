package LuckyVicky.backend.enhance.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EnhanceRequestDto {

    @Schema(description = "ItemEnhanceReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemEnhanceReqDto {

        @Schema(description = "강화할 상품 id")
        private Long itemId;

        @Schema(description = "사용할 보석 종류 (S, A, B)")
        private String jewelType;

    }

}