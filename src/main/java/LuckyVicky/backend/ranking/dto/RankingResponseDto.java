package LuckyVicky.backend.ranking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RankingResponseDto {

    @Schema(description = "UserRankingResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserRankingResDto {
        @Schema(description = "유저 닉네임")
        private String nickname;

        @Schema(description = "유저 랭킹")
        private Integer ranking;

        @Schema(description = "강화 레벨")
        private Integer enhanceLevel;

        @Schema(description = "상품 수령 여부")
        private Boolean isGet;
    }

    // 상품별 랭킹 정보
    @Schema(description = "ItemRankingResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemRankingResDto {
        @Schema(description = "유저 랭킹 리스트")
        private List<UserRankingResDto> userRankingResDtoList;

        @Schema(description = "상품 이름")
        private String itemName;

        @Schema(description = "사용자 랭킹")
        private Integer myRanking;
    }

    // 주차별 랭킹 정보
    @Schema(description = "ItemRankingResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WeekRankingResDto {
        @Schema(description = "강화 월 & 주차")
        private String enhanceMonthWeek;

        @Schema(description = "강화 시작일")
        private LocalDate enhanceStartDate;

        @Schema(description = "강화 종료일")
        private LocalDate enhanceEndDate;

        @Schema(description = "상품별 랭킹 리스트")
        private List<ItemRankingResDto> itemRankingResDtoList;
    }

}
