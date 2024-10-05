package LuckyVicky.backend.item.converter;

import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.dto.ItemRequestDto;
import LuckyVicky.backend.item.dto.ItemResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ItemConverter {

    public Item toEntity(ItemRequestDto requestDto, String imageUrl) {
        LocalDate availableDate = (requestDto.getAvailableDate() != null)
                ? LocalDate.parse(requestDto.getAvailableDate())
                : null;

        return Item.builder()
                .name(requestDto.getItemName())
                .description(requestDto.getItemDescription())
                .availableDate(availableDate)
                .quantity(requestDto.getQuantity())
                .imageUrl(imageUrl)
                .likeCount(0)
                .build();
    }

    public ItemResponseDto toDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .availableDate(item.getAvailableDate())
                .quantity(item.getQuantity())
                .imageUrl(item.getImageUrl())
                .build();
    }

    public static ItemRequestDto itemRequestDto(String itemName, String itemDescription, String availableDate, String quantity, MultipartFile imageFile) {
        return ItemRequestDto.builder()
                .itemName(itemName)
                .itemDescription(itemDescription)
                .availableDate(availableDate)
                .quantity(quantity)  // 문자열로 처리
                .imageFile(imageFile)
                .build();
    }
}
