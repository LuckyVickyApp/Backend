package LuckyVicky.backend.item.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.item.converter.ItemConverter;
import LuckyVicky.backend.item.dto.ItemRequestDto;
import LuckyVicky.backend.item.dto.ItemResponseDto;
import LuckyVicky.backend.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Tag(name = "상품", description = "상품 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/item")
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "상품 등록", description = "상품을 등록하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 등록 완료")
    })
    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public ApiResponse<ItemResponseDto> createItem(
            @RequestPart("itemName") String itemName,
            @RequestPart(value = "itemDescription", required = false) String itemDescription,
            @RequestPart(value = "availableDate", required = false) String availableDate,
            @RequestPart(value = "quantity", required = false) String quantity,  // 수량을 문자열로 받음
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        // DTO 빌드
        ItemRequestDto requestDto = ItemConverter.itemRequestDto(itemName, itemDescription, availableDate, quantity, imageFile);

        // 서비스 로직 호출
        ItemResponseDto createdItem = itemService.createItem(requestDto);

        return ApiResponse.onSuccess(SuccessCode.ITEM_CREATE_SUCCESS, createdItem);
    }

    @Operation(summary = "상품 리스트 조회", description = "모든 상품을 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 리스트 조회 완료")
    })
    @GetMapping("")
    public ResponseEntity<List<ItemResponseDto>> getItems() {
        List<ItemResponseDto> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }


    @Operation(summary = "특정 상품 조회", description = "상품명을 기준으로 특정 상품을 조회합니다.")
    @GetMapping("/name/{name}")
    public ApiResponse<ItemResponseDto> getItemByName(
            @Parameter(description = "상품명", required = true, example = "상품명") @PathVariable("name") String name) {
        ItemResponseDto item = itemService.getItemByName(name);
        return ApiResponse.onSuccess(SuccessCode.ITEM_VIEW_SUCCESS, item);
    }

    @Operation(summary = "특정 상품 삭제", description = "상품명을 기준으로 특정 상품을 삭제합니다.")
    @DeleteMapping("/name/{name}")
    public ApiResponse<String> deleteItemByName(
            @Parameter(description = "상품명", required = true, example = "상품명") @PathVariable("name") String name) {

        // 서비스 호출로 상품 삭제
        itemService.deleteItemByName(name);
        return ApiResponse.onSuccess(SuccessCode.ITEM_DELETE_SUCCESS, "상품이 성공적으로 삭제되었습니다.");
    }
}

