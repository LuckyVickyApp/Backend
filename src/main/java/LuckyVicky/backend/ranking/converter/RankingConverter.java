package LuckyVicky.backend.ranking.converter;

import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.ItemRankingResDto;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.UserRankingResDto;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.WeekRankingResDto;
import java.time.LocalDate;
import java.util.List;

public class RankingConverter {
    private RankingConverter() {
        throw new UnsupportedOperationException("Converter class는 인스턴스화가 불가능합니다.");
    }

    public static UserRankingResDto userRankingResDto(EnhanceItem enhanceItem) {
        return UserRankingResDto.builder()
                .nickname(enhanceItem.getUser().getNickname())
                .ranking(enhanceItem.getRanking())
                .enhanceLevel(enhanceItem.getEnhanceLevel())
                .isGet(enhanceItem.getIsGet())
                .build();
    }

    public static ItemRankingResDto itemRankingResDto(Item item, Integer myRanking,
                                                      List<UserRankingResDto> userRankingResDtoList) {
        return ItemRankingResDto.builder()
                .userRankingResDtoList(userRankingResDtoList)
                .itemName(item.getName())
                .myRanking(myRanking)
                .build();
    }

    public static WeekRankingResDto weekRankingResDto(String enhanceMonthWeek,
                                                      List<ItemRankingResDto> itemRankingResDtoList,
                                                      LocalDate enhanceStartDate, LocalDate enhanceEndDate) {
        return WeekRankingResDto.builder()
                .itemRankingResDtoList(itemRankingResDtoList)
                .enhanceMonthWeek(enhanceMonthWeek)
                .enhanceStartDate(enhanceStartDate)
                .enhanceEndDate(enhanceEndDate)
                .build();
    }
}
