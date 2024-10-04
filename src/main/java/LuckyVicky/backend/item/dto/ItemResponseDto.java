package LuckyVicky.backend.item.dto;

import lombok.*;

import java.time.LocalDate;

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
    private LocalDate availableDate;
}
