package LuckyVicky.backend.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "상품 등록/수정 요청 DTO")
public class ItemRequestDto {

    @Schema(description = "상품명", example = "강화된 검")
    @NotNull(message = "상품명은 필수입니다.")
    private String itemName;

    @Schema(description = "상품 설명", example = "이 검은 매우 강력합니다.")
    private String itemDescription;

    @Schema(description = "상품 등록 날짜", example = "2024-10-10")
    private String availableDate;

    @Schema(description = "상품 수량", example = "100")
    private String quantity;

    @Schema(description = "상품 이미지 파일", type = "file")
    private MultipartFile imageFile;
}
