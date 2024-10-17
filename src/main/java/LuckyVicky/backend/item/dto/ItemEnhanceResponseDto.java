package LuckyVicky.backend.item.dto;

import LuckyVicky.backend.user.dto.UserJewelResponseDto.UserJewelResDto;
import LuckyVicky.backend.item.dto.ItemResponseDto.ItemForEnhanceResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ItemEnhanceResponseDto {

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
}
