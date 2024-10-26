package LuckyVicky.backend.item.dto;

import LuckyVicky.backend.user.dto.UserJewelResponseDto.UserJewelResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDto {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer quantity;
    private LocalDate enhanceStartDate;

}
