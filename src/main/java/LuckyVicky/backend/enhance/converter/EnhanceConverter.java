package LuckyVicky.backend.enhance.converter;

import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.EnhanceExecuteResDto;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.enhance.domain.EnhanceResult;
import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.ItemForEnhanceResDto;
import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.ItemEnhanceResDto;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.dto.UserJewelResponseDto.UserJewelResDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EnhanceConverter {

    public static ItemForEnhanceResDto itemForEnhanceResDto(Item item, Integer enhanceLevel, Boolean isItemLike) {
        return ItemForEnhanceResDto.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .itemImage(item.getImageUrl())
                .itemLikeCount(item.getLikeCount())
                .itemEnhanceLevel(enhanceLevel)
                .isLike(isItemLike)
                .build();
    }

    public static UserJewelResDto userJewelResDto(UserJewel userJewel) {
        return UserJewelResDto.builder()
                .jewelType(userJewel.getJewelType())
                .count(userJewel.getCount())
                .build();
    }

    public static ItemEnhanceResDto itemEnhanceResDto(List<ItemForEnhanceResDto> itemForEnhanceResDtoList,
                                                      List<UserJewelResDto> userJewelResDtoList) {
        return ItemEnhanceResDto.builder()
                .itemForEnhanceResDtoList(itemForEnhanceResDtoList)
                .userJewelResDtoList(userJewelResDtoList)
                .build();
    }

    public static EnhanceExecuteResDto itemEnhanceExecuteResDto(EnhanceItem enhanceItem, EnhanceResult enhanceResult, Integer userRankingChange) {
        return EnhanceExecuteResDto.builder()
                .enhanceResult(enhanceResult)
                .userRanking(enhanceItem.getRanking())
                .userRankingChange(userRankingChange)
                .enhanceLevel(enhanceItem.getEnhanceLevel())
                .image(enhanceItem.getItem().getImageUrl())
                .build();
    }

    public static EnhanceItem createEnhanceItem(User user, Item item, Integer lastRanking) {
        return EnhanceItem.builder()
                .user(user)
                .item(item)
                .attemptCount(0)
                .enhanceLevel(1)
                .ranking(lastRanking)
                .enhanceLevelReachedAt(LocalDateTime.now())
                .isGet(lastRanking <= item.getQuantity())
                .build();
    }
}
