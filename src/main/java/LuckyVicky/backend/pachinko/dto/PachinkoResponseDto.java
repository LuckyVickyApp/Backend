package LuckyVicky.backend.pachinko.dto;

import LuckyVicky.backend.enhance.domain.JewelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PachinkoResponseDto {

    @Schema(description = "PachinkoChosenResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PachinkoChosenResDto {
        @Schema(description = "내가 선택한 칸들")
        private Set<Integer> meChosen;

        @Schema(description = "다른 유저들이 선택한 칸들 반환")
        private Set<Integer> othersChosen;
    }

    @Schema(description = "PachinkoRewardResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PachinkoUserRewardResDto {
        @Schema(description = "S급 보석")
        private Long jewelS;

        @Schema(description = "A급 보석")
        private Long jewelA;

        @Schema(description = "B급 보석")
        private Long jewelB;
    }

    @Schema(description = "PachinkoSquareRewardResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PachinkoSquareRewardResDto {

        @Schema(description = "칸 번호")
        private Integer squareNum;

        @Schema(description = "보석 종류")
        private JewelType jewelType;

        @Schema(description = "보석 개수")
        private int JewelNum;

    }

    @Schema(description = "PachinkoRewardResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PachinkoRewardResDto {

        @Schema(description = "유저의 보상")
        private PachinkoUserRewardResDto pachinkoUserRewardResDto;

        @Schema(description = "칸 각각의 보상")
        private List<PachinkoSquareRewardResDto> pachinkoSquareRewardResDtoList;

    }

}
