package LuckyVicky.backend.enhance.controller;

import LuckyVicky.backend.enhance.converter.EnhanceConverter;
import LuckyVicky.backend.enhance.service.EnhanceService;
import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.enhance.domain.EnhanceResult;
import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.enhance.dto.EnhanceRequestDto.ItemEnhanceReqDto;
import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.EnhanceExecuteResDto;
import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.ItemEnhanceResDto;
import LuckyVicky.backend.item.service.ItemService;
import LuckyVicky.backend.enhance.service.EnhanceItemService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import LuckyVicky.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "강화")
@RestController
@RequestMapping("/enhance")
@RequiredArgsConstructor
public class EnhanceController {
    private final UserService userService;
    private final ItemService itemService;
    private final EnhanceItemService enhanceItemService;
    private final EnhanceService itemEnhanceService;
    private final EnhanceConverter enhanceConverter;

    @Operation(summary = "강화 화면 요소 반환 메서드", description = "강화 화면에 필요한 요소들을 반환하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ENHANCE_2001", description = "상품 강화 화면에 필요한 요소들이 반환되었습니다.")
    })
    @GetMapping("")
    public ApiResponse<ItemEnhanceResDto> getEnhanceDetail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());

        // 현재 강화 가능한 상품 리스트
        List<Item> itemList = itemService.getEnhanceItemList();

        return ApiResponse.onSuccess(SuccessCode.ENHANCE_LIST_SUCCESS, enhanceConverter.itemEnhanceResDto(user, itemList));

    }

    @Operation(summary = "강화 시도 메서드", description = "강화 시도 후, 결과를 반환하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ENHANCE_2002", description = "강화 시도 후, 결과 반환이 완료되었습니다.")
    })
    @PatchMapping("/execute")
    public ApiResponse<EnhanceExecuteResDto> getEnhanceResult(
            @RequestBody ItemEnhanceReqDto itemEnhanceReqDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        System.out.println(itemEnhanceReqDto.getItemId());
        System.out.println(itemEnhanceReqDto.getJewelType());

        // 강화할 Entity 찾기
        User user = userService.findByUserName(customUserDetails.getUsername());
        Item item = itemService.findById(itemEnhanceReqDto.getItemId());
        EnhanceItem enhanceItem = enhanceItemService.findByUserAndItem(user, item);

        // 강화
        EnhanceResult enhanceResult = itemEnhanceService.getItemEnhanceResult(enhanceItem, itemEnhanceReqDto.getJewelType());

        return ApiResponse.onSuccess(SuccessCode.ENHANCE_RESULT_SUCCESS, EnhanceConverter.itemEnhanceExecuteResDto(enhanceItem, enhanceResult));

    }

}
