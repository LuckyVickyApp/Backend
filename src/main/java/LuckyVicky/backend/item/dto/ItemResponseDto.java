package LuckyVicky.backend.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ItemResponseDto {

    @Schema(description = "ItemDetailResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDetailResDto {
        private Long id;
        private String name;
        private String description;
        private String imageUrl;
        private Integer quantity;
        private LocalDate enhanceStartDate;
    }

    @Schema(description = "ItemDescriptionResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDescriptionResDto {
        @Schema(description = "제품 설명 key")
        private String key;

        @Schema(description = "제품 설명 value")
        private String value;
    }

    @Schema(description = "ItemDescriptionResListDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDescriptionResListDto {
        @Schema(description = "제품 설명")
        private List<ItemDescriptionResDto> itemDescriptionResDtoList;
    }

}
