package LuckyVicky.backend.ranking.converter;

import static LuckyVicky.backend.global.util.Constant.CONVERTER_INSTANTIATION_NOT_ALLOWED;

import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.CurrentItemRankingResDto;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.ItemRankingResDto;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.UserRankingResDto;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.WeekRankingResDto;
import java.time.LocalDate;
import java.util.List;

public class RankingConverter {
    private RankingConverter() {
        throw new UnsupportedOperationException(CONVERTER_INSTANTIATION_NOT_ALLOWED);
    }

    public static UserRankingResDto userRankingResDto(EnhanceItem enhanceItem) {
        return UserRankingResDto.builder()
                .nickname(enhanceItem.getUser().getNickname())
                .ranking(enhanceItem.getRanking())
                .enhanceLevel(enhanceItem.getEnhanceLevel())
                .isGet(enhanceItem.getIsGet())
                .profile(enhanceItem.getUser().getProfileImage())
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

    public static CurrentItemRankingResDto currentItemRankingResDto(Item item,
                                                                    List<UserRankingResDto> userRankingResDtoList,
                                                                    EnhanceItem enhanceItem) {
        return CurrentItemRankingResDto.builder()
                .itemName(item.getName())
                .enhanceEndDate(item.getEnhanceEndDate())
                .myRanking(enhanceItem.getRanking())
                .userRankingResDtoList(userRankingResDtoList)
                .build();
    }

}
