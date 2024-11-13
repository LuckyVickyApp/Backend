package LuckyVicky.backend.enhance.service;

import LuckyVicky.backend.enhance.converter.EnhanceConverter;
import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.ItemEnhanceResDto;
import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.ItemForEnhanceResDto;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.enhance.repository.EnhanceItemRepository;
import LuckyVicky.backend.item.domain.ItemLike;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.dto.UserJewelResponseDto.UserJewelResDto;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnhanceItemService {
    private static final int DEFAULT_ENHANCE_LEVEL = 1;
    private final EnhanceItemRepository enhanceItemRepository;

    public EnhanceItem findByUserAndItem(User user, Item item) {
        return enhanceItemRepository.findByUserAndItem(user, item)
                .orElseThrow(()-> new GeneralException(ErrorCode.ENHANCE_ITEM_NOT_FOUND));
    }

    public EnhanceItem findByUserAndItemOrCreateEnhanceItem(User user, Item item) {
        return enhanceItemRepository.findByUserAndItem(user, item)
                // 없으면 새로운 Entity 생성
                .orElseGet(() -> {
                    Integer lastRanking = enhanceItemRepository.countByItem(item) + 1;
                    EnhanceItem newEnhanceItem = EnhanceConverter.createEnhanceItem(user, item, lastRanking);
                    return enhanceItemRepository.save(newEnhanceItem);
                });
    }

    private Integer getEnhanceLevel(User user, Item item) {
        return enhanceItemRepository.findByUserAndItem(user, item)
                .map(EnhanceItem::getEnhanceLevel)
                .orElse(DEFAULT_ENHANCE_LEVEL);
    }

    public ItemForEnhanceResDto getItemForEnhanceResDto(User user, Item item) {
        Integer enhanceLevel = getEnhanceLevel(user, item);

        Boolean isItemLike = user.getItemLikeList().stream()
                .anyMatch(like -> like.getItem() == item);

        return EnhanceConverter.itemForEnhanceResDto(item, enhanceLevel, isItemLike);
    }

    @Transactional
    public ItemEnhanceResDto getItemEnhanceResDto(User user, List<Item> itemList) {
        List<ItemForEnhanceResDto> itemForEnhanceResDtoList
                = itemList.stream()
                .map(item -> getItemForEnhanceResDto(user, item))
                .toList();

        List<UserJewelResDto> userJewelResDtoList
                = user.getUserJewelList().stream()
                .map(EnhanceConverter::userJewelResDto)
                .toList();

        return EnhanceConverter.itemEnhanceResDto(itemForEnhanceResDtoList, userJewelResDtoList);
    }
}
