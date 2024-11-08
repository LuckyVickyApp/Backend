package LuckyVicky.backend.ranking.service;

import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.enhance.repository.EnhanceItemRepository;
import LuckyVicky.backend.enhance.service.EnhanceItemService;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.repository.ItemRepository;
import LuckyVicky.backend.ranking.converter.RankingConverter;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.CurrentItemRankingResDto;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.ItemRankingResDto;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.UserRankingResDto;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.WeekRankingResDto;
import LuckyVicky.backend.user.domain.User;
import jakarta.transaction.Transactional;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final EnhanceItemRepository enhanceItemRepository;
    private final ItemRepository itemRepository;
    private final EnhanceItemService enhanceItemService;

    // 특정 날짜를 "?월 ?주차" 형식으로 변환
    public String getMonthWeekString(LocalDate date) {
        int month = date.getMonthValue();

        // 첫째 주 기준 설정 (Locale 설정에 따라 다를 수 있음, 기본적으로 월요일을 기준으로 함)
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekOfMonth = date.get(weekFields.weekOfMonth());

        return month + "월 " + weekOfMonth + "주차";
    }

    public ItemRankingResDto getItemRankingResDto(User user, Item item) {
        List<EnhanceItem> enhanceItemList =
                enhanceItemRepository.findEnhanceItemsByItemOrderByEnhanceLevelAndReachedTime(item);

        Integer myRanking = enhanceItemService.findByUserAndItem(user, item).getRanking();

        List<UserRankingResDto> userRankingResDtoList
                = enhanceItemList.stream()
                .map(RankingConverter::userRankingResDto)
                .toList();

        return RankingConverter.itemRankingResDto(item, myRanking ,userRankingResDtoList);
    }

    public WeekRankingResDto getWeekRankingResDto(User user, List<Item> weekItemList, LocalDate date) {
        if (weekItemList.isEmpty()) {
            throw new GeneralException(ErrorCode.RANKING_WEEK_ITEM_LIST_EMPTY);
        }

        List<ItemRankingResDto> itemRankingResDtoList = weekItemList.stream()
                .map(item -> getItemRankingResDto(user, item))
                .toList();

        String enhanceMonthWeek = getMonthWeekString(date);
        LocalDate enhanceStartDate = weekItemList.get(0).getEnhanceStartDate();
        LocalDate enhanceEndDate = weekItemList.get(0).getEnhanceEndDate();

        return RankingConverter.weekRankingResDto(enhanceMonthWeek, itemRankingResDtoList, enhanceStartDate, enhanceEndDate);
    }

    public CurrentItemRankingResDto getCurrentItemRankingResDto(Item item, EnhanceItem enhanceItem) {
        List<EnhanceItem> enhanceItemList =
                enhanceItemRepository.findEnhanceItemsByItemOrderByEnhanceLevelAndReachedTime(item);

        List<UserRankingResDto> userRankingResDtoList
                 = enhanceItemList.stream()
                .map(RankingConverter::userRankingResDto)
                .toList();

        return RankingConverter.currentItemRankingResDto(item, userRankingResDtoList, enhanceItem);
    }
}
