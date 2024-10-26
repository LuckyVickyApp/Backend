package LuckyVicky.backend.ranking.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.service.ItemService;
import LuckyVicky.backend.ranking.converter.RankingConverter;
import LuckyVicky.backend.ranking.dto.RankingResponseDto.WeekRankingResDto;
import LuckyVicky.backend.ranking.service.RankingService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import LuckyVicky.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "랭킹")
@RestController
@RequestMapping("ranking")
@RequiredArgsConstructor
public class RankingController {

    private final UserService userService;
    private final RankingService rankingService;
    private final ItemService itemService;

    @Operation(summary = "현재 주차 상품 별 랭킹 반환 매서드", description = "현재 주차 상품 별 랭킹을 반환하는 매서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RANKING_2001", description = "현재 주차 상품 별 랭킹을 반환이 완료되었습니다.")
    })
    @GetMapping("/week/current")
    public ApiResponse<WeekRankingResDto> getCurrentWeekItemRanking(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());

        LocalDate localDate = LocalDate.now();

        List<Item> currentWeekItemList = itemService.getWeekItemList(localDate);

        return ApiResponse.onSuccess(SuccessCode.RANKING_CURRENT_WEEK_SUCCESS, rankingService.getWeekRankingResDto(user, currentWeekItemList, localDate));

    }

    @Operation(summary = "이전 주차 상품 별 랭킹 반환 매서드", description = "이전 주차 상품 별 랭킹을 반환하는 매서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RANKING_2002", description = "이전 주차 상품 별 랭킹을 반환이 완료되었습니다.")
    })
    @Parameters({
            @Parameter(name = "enhanceStartDate", description = "특정 주차의 강화 시작일 (예시: 2024-10-26)")
    })
    @GetMapping("/week/previous")
    public ApiResponse<WeekRankingResDto> getPreviousWeekItemRanking(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(name = "enhanceStartDate") LocalDate enhanceStartDate
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());

        LocalDate previousWeekDate = enhanceStartDate.minusWeeks(1);

        List<Item> weekItemList = itemService.getWeekItemList(previousWeekDate);

        return ApiResponse.onSuccess(SuccessCode.RANKING_PREVIOUS_WEEK_SUCCESS, rankingService.getWeekRankingResDto(user, weekItemList, previousWeekDate));

    }

    @Operation(summary = "다음 주차 상품 별 랭킹 반환 매서드", description = "다음 주차 상품 별 랭킹을 반환하는 매서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RANKING_2003", description = "다음 주차 상품 별 랭킹을 반환이 완료되었습니다.")
    })
    @Parameters({
            @Parameter(name = "enhanceStartDate", description = "특정 주차의 강화 시작일 (예시: 2024-10-26)")
    })
    @GetMapping("/week/next")
    public ApiResponse<WeekRankingResDto> getNextWeekItemRanking(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(name = "enhanceStartDate") LocalDate enhanceStartDate
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());

        LocalDate nextWeekDate = enhanceStartDate.plusWeeks(1);

        List<Item> weekItemList = itemService.getWeekItemList(nextWeekDate);

        return ApiResponse.onSuccess(SuccessCode.RANKING_NEXT_WEEK_SUCCESS, rankingService.getWeekRankingResDto(user, weekItemList, nextWeekDate));

    }

}
