package LuckyVicky.backend.enhance.converter;

import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.EnhanceExecuteResDto;
import LuckyVicky.backend.enhance.repository.EnhanceItemRepository;
import LuckyVicky.backend.enhance.service.EnhanceItemService;
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
    private final EnhanceItemService enhanceItemService;

    public  ItemForEnhanceResDto itemForEnhanceResDto(User user, Item item) {

        Integer enhanceLevel = enhanceItemService.findByUserAndItem(user, item).getEnhanceLevel();

        return ItemForEnhanceResDto.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .itemImage(item.getImageUrl())
                .itemLikeCount(item.getLikeCount())
                .itemEnhanceLevel(enhanceLevel)
                .build();
    }

    public static UserJewelResDto userJewelResDto(UserJewel userJewel) {
        return UserJewelResDto.builder()
                .jewelType(userJewel.getJewelType())
                .count(userJewel.getCount())
                .build();
    }

    public ItemEnhanceResDto itemEnhanceResDto(User user, List<Item> itemList) {

        List<ItemForEnhanceResDto> itemForEnhanceResDtoList
                = itemList.stream()
                .map(item -> itemForEnhanceResDto(user, item))
                .toList();

        List<UserJewelResDto> userJewelResDtoList
                = user.getUserJewelList().stream()
                .map(EnhanceConverter::userJewelResDto)
                .toList();

        return ItemEnhanceResDto.builder()
                .itemForEnhanceResDtoList(itemForEnhanceResDtoList)
                .userJewelResDtoList(userJewelResDtoList)
                .build();
    }

    public static EnhanceExecuteResDto itemEnhanceExecuteResDto(EnhanceItem enhanceItem, EnhanceResult enhanceResult) {
        return EnhanceExecuteResDto.builder()
                .enhanceResult(enhanceResult)
                .userRanking(enhanceItem.getRanking())
                .enhanceLevel(enhanceItem.getEnhanceLevel())
                .image(enhanceItem.getItem().getImageUrl())
                .build();
    }

    public static EnhanceItem createEnhanceItem(User user, Item item) {
        return EnhanceItem.builder()
                .user(user)
                .item(item)
                .attemptCount(0)
                .enhanceLevel(1)
                .ranking(0)
                .enhanceLevelReachedAt(LocalDateTime.now())
                .build();
    }
}
