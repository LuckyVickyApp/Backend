package LuckyVicky.backend.item.converter;

import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.dto.ItemRequestDto;
import LuckyVicky.backend.item.dto.ItemResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ItemConverter {

    public Item toEntity(ItemRequestDto requestDto, String imageUrl) {
        LocalDate availableDate = (requestDto.getAvailableDate() != null)
                ? LocalDate.parse(requestDto.getAvailableDate())
                : null;

        return Item.builder()
                .name(requestDto.getItemName())
                .description(requestDto.getItemDescription())
                .availableDate(availableDate)
                .quantity(requestDto.getQuantity())  // quantity는 문자열로 변환
                .imageUrl(imageUrl)
                .build();
    }

    public ItemResponseDto toDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .availableDate(item.getAvailableDate())
                .quantity(Integer.valueOf(item.getQuantity()))
                .imageUrl(item.getImageUrl())
                .build();
    }
}
