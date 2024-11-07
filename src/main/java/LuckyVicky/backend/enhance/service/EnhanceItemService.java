package LuckyVicky.backend.enhance.service;

import LuckyVicky.backend.enhance.converter.EnhanceConverter;
import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.ItemEnhanceResDto;
import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.ItemForEnhanceResDto;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.enhance.repository.EnhanceItemRepository;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.dto.UserJewelResponseDto.UserJewelResDto;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnhanceItemService {
    private final EnhanceItemRepository enhanceItemRepository;

    public EnhanceItem findByUserAndItem(User user, Item item) {
        return enhanceItemRepository.findByUserAndItem(user, item)
                // 없으면 새로운 Entity 생성
                .orElseGet(() -> {
                    EnhanceItem newEnhanceItem = EnhanceConverter.createEnhanceItem(user, item);
                    return enhanceItemRepository.save(newEnhanceItem);
                });
    }

    public ItemForEnhanceResDto getItemForEnhanceResDto(User user, Item item) {
        Integer enhanceLevel = findByUserAndItem(user, item).getEnhanceLevel();

        return EnhanceConverter.itemForEnhanceResDto(user, item, enhanceLevel);
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
