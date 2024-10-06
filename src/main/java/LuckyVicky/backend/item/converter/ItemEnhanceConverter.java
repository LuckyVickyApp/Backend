package LuckyVicky.backend.item.converter;

import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.domain.UserItem;
import LuckyVicky.backend.item.dto.ItemEnhanceResponseDto.ItemEnhanceResDto;
import LuckyVicky.backend.item.dto.ItemResponseDto.ItemForEnhanceResDto;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.dto.UserJewelResponseDto.UserJewelResDto;
import LuckyVicky.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemEnhanceConverter {
    private final UserRepository userRepository;

    public static ItemForEnhanceResDto itemForEnhanceResDto(User user, Item item) {
        Integer itemEnhanceLevel = user.getUserItemList().stream()
                .filter(userItem -> userItem.getItem().equals(item))
                .map(UserItem::getEnhanceLevel)
                .findFirst()
                .orElse(0);

        return ItemForEnhanceResDto.builder()
                .id(item.getId())
                .itemName(item.getName())
                .itemImage(item.getImageUrl())
                .itemLikeCount(item.getLikeCount())
                .itemEnhanceLevel(itemEnhanceLevel)
                .build();
    }

    public static UserJewelResDto userJewelResDto(UserJewel userJewel) {
        return UserJewelResDto.builder()
                .jewelType(userJewel.getJewelType())
                .count(userJewel.getCount())
                .build();
    }

    public static ItemEnhanceResDto itemEnhanceResDto(User user, List<Item> itemList) {

        List<ItemForEnhanceResDto> itemForEnhanceResDtoList
                = itemList.stream()
                .map(item -> itemForEnhanceResDto(user, item))
                .toList();

        List<UserJewelResDto> userJewelResDtoList
                = user.getUserJewelList().stream()
                .map(ItemEnhanceConverter::userJewelResDto)
                .toList();

        return ItemEnhanceResDto.builder()
                .itemForEnhanceResDtoList(itemForEnhanceResDtoList)
                .userJewelResDtoList(userJewelResDtoList)
                .build();
    }
}
