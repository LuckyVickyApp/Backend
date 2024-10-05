package LuckyVicky.backend.item.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.item.service.ItemLikeService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import LuckyVicky.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 좋아요", description = "상품 좋아요 관련 api입니다.")
@RestController
@RequestMapping("/item/{item-id}/like")
@RequiredArgsConstructor
public class ItemLikeController {
    private final UserService userService;
    private final ItemLikeService itemLikeService;

    @Operation(summary = "상품 좋아요 메서드", description = "상품 좋아요하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ITEM_LIKE_2001", description = "상품 좋아요가 완료되었습니다.")
    })
    @PostMapping("/create")
    public ApiResponse<Integer> createLike(
            @PathVariable(name = "item-id") Long itemId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        Integer likeCount = itemLikeService.createLikeAndRetrieveCount(itemId, user);

        return ApiResponse.onSuccess(SuccessCode.ITEM_LIKE_SUCCESS, likeCount);
    }

    @Operation(summary = "상품 좋아요 취소 메서드", description = "상품 좋아요를 취소하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ITEM_LIKE_2002", description = "상품 좋아요 취소가 완료되었습니다.")
    })
    @DeleteMapping("/delete")
    public ApiResponse<Integer> deleteLike(
            @PathVariable(name = "item-id") Long itemId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        Integer likeCount = itemLikeService.deleteLikeAndRetrieveCount(itemId, user);

        return ApiResponse.onSuccess(SuccessCode.ITEM_UNLIKE_SUCCESS, likeCount);
    }
}
