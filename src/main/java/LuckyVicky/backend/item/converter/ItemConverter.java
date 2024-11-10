package LuckyVicky.backend.item.converter;

import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.dto.ItemRequestDto;
import LuckyVicky.backend.item.dto.ItemResponseDto.ItemDescriptionResDto;
import LuckyVicky.backend.item.dto.ItemResponseDto.ItemDescriptionResListDto;
import LuckyVicky.backend.item.dto.ItemResponseDto.ItemDetailResDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class ItemConverter {
    private ItemConverter() {
        throw new UnsupportedOperationException("Converter class는 인스턴스화가 불가능합니다.");
    }

    public Item toEntity(ItemRequestDto requestDto, String imageUrl) {
        LocalDate availableDate = (requestDto.getAvailableDate() != null)
                ? LocalDate.parse(requestDto.getAvailableDate())
                : null;

        return Item.builder()
                .name(requestDto.getItemName())
                .enhanceStartDate(availableDate)
                .quantity(requestDto.getQuantity())
                .imageUrl(imageUrl)
                .likeCount(0)
                .build();
    }

    public ItemDetailResDto toDto(Item item) {
        return ItemDetailResDto.builder()
                .id(item.getId())
                .name(item.getName())
                .enhanceStartDate(item.getEnhanceStartDate())
                .quantity(item.getQuantity())
                .imageUrl(item.getImageUrl())
                .build();
    }

    public static ItemRequestDto itemRequestDto(String itemName, String itemDescription, String availableDate,
                                                Integer quantity, MultipartFile imageFile) {
        return ItemRequestDto.builder()
                .itemName(itemName)
                .itemDescription(itemDescription)
                .availableDate(availableDate)
                .quantity(quantity)  // 문자열로 처리
                .imageFile(imageFile)
                .build();
    }

    public static ItemDescriptionResListDto itemDescriptionResListDto(Item item, List<String> keys,
                                                                      List<String> values) {
        List<ItemDescriptionResDto> itemDescriptionResDtoList = new ArrayList<>();

        int size = Math.min(keys.size(), values.size());

        for (int i = 0; i < size; i++) {
            itemDescriptionResDtoList.add(
                    ItemDescriptionResDto.builder()
                            .key(keys.get(i))
                            .value(values.get(i))
                            .build()
            );
        }

        return ItemDescriptionResListDto.builder()
                .image(item.getImageUrl())
                .name(item.getName())
                .price(item.getPrice())
                .itemDescriptionResDtoList(itemDescriptionResDtoList)
                .build();
    }
}
