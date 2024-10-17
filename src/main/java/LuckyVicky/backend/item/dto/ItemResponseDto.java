package LuckyVicky.backend.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDto {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String quantity;
    private LocalDate enhanceStartDate;

    @Schema(description = "ItemForEnhanceResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemForEnhanceResDto {
        @Schema(description = "상품 id")
        private Long id;

        @Schema(description = "상품 이름")
        private String itemName;

        @Schema(description = "상품 이미지")
        private String itemImage;

        @Schema(description = "상품 현재 강화 레벨")
        private Integer itemEnhanceLevel;

        @Schema(description = "상품 좋아요 수")
        private Integer itemLikeCount;
    }
}
