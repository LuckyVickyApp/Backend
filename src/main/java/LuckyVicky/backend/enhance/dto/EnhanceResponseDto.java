package LuckyVicky.backend.enhance.dto;

import LuckyVicky.backend.enhance.domain.EnhanceResult;
import LuckyVicky.backend.user.dto.UserJewelResponseDto.UserJewelResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EnhanceResponseDto {

    @Schema(description = "ItemForEnhanceResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemForEnhanceResDto {
        @Schema(description = "상품 id")
        private Long itemId;

        @Schema(description = "상품 이름")
        private String itemName;

        @Schema(description = "상품 이미지")
        private String itemImage;

        @Schema(description = "상품 현재 강화 레벨")
        private Integer itemEnhanceLevel;

        @Schema(description = "상품 좋아요 수")
        private Integer itemLikeCount;
    }

    @Schema(description = "ItemEnhanceResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemEnhanceResDto {
        @Schema(description = "상품 리스트")
        private List<ItemForEnhanceResDto> itemForEnhanceResDtoList;

        @Schema(description = "유저 보유 보석 리스트")
        private List<UserJewelResDto> userJewelResDtoList;
    }

    @Schema(description = "EnhanceExecuteResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EnhanceExecuteResDto {
        @Schema(description = "강화 결과")
        private EnhanceResult enhanceResult;

        @Schema(description = "현재 유저 랭킹")
        private Integer userRanking;

        @Schema(description = "유저 랭킹 변화 수치")
        private Integer userRankingChange;

        @Schema(description = "현재 강화 레벨")
        private Integer enhanceLevel;

        @Schema(description = "상품 이미지")
        private String image;
    }
}
