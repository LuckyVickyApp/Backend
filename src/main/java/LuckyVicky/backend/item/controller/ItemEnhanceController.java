package LuckyVicky.backend.item.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.item.converter.ItemEnhanceConverter;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.dto.ItemEnhanceResponseDto.ItemEnhanceResDto;
import LuckyVicky.backend.item.service.ItemService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import LuckyVicky.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "강화")
@RestController
@RequestMapping("/item/enhance")
@RequiredArgsConstructor
public class ItemEnhanceController {
    private final UserService userService;
    private final ItemService itemService;

    @Operation(summary = "강화 화면 요소 반환 메서드", description = "강화 화면에 필요한 요소들을 반환하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ITEM_2003", description = "상품 강화 화면에 필요한 요소들이 반환 완료되었습니다.")
    })
    @GetMapping("")
    public ApiResponse<ItemEnhanceResDto> getEnhanceDetail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());

        // 현재 강화 가능한 상품 리스트
        List<Item> itemList = itemService.getEnhanceItemList();

        System.out.println(itemList.size());

        return ApiResponse.onSuccess(SuccessCode.ITEM_ENHANCE_SUCCESS, ItemEnhanceConverter.itemEnhanceResDto(user, itemList));

    }
}
