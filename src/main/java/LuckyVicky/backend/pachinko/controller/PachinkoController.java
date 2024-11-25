package LuckyVicky.backend.pachinko.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.pachinko.converter.PachinkoConverter;
import LuckyVicky.backend.pachinko.domain.Pachinko;
import LuckyVicky.backend.pachinko.dto.PachinkoResponseDto.PachinkoChosenResDto;
import LuckyVicky.backend.pachinko.dto.PachinkoResponseDto.PachinkoRewardResDto;
import LuckyVicky.backend.pachinko.service.PachinkoService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import LuckyVicky.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "게임", description = "게임 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game/pachinko")
public class PachinkoController {

    private final PachinkoService pachinkoService;
    private final UserService userService;

    @Operation(summary = "빠칭코 선택된 칸 반환", description = "빠칭코에서 선택 완료된 칸 반환하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PACHINKO_2001", description = "빠칭코 선택완료 칸 확인 성공"),
    })
    @GetMapping("/chosen-squares")
    public ApiResponse<PachinkoChosenResDto> SelectedSquares(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        Set<Integer> chosenSquares = pachinkoService.getSelectedSquares();
        Long currentRound = pachinkoService.getCurrentRound();

        List<Integer> meChosenList = pachinkoService.getMeChosen(user);
        Set<Integer> meChosenSet = new HashSet<>(meChosenList);
        meChosenSet.remove(0);

        return ApiResponse.onSuccess(SuccessCode.PACHINKO_GET_SQUARES_SUCCESS,
                PachinkoConverter.pachinkoChosenResDto(currentRound, meChosenSet, chosenSquares));
    }

    @Operation(summary = "빠칭코 첫 게임 시작", description = "빠칭코 첫 게임의 각 칸에 대한 보상을 정하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PACHINKO_2002", description = "빠칭코 첫 게임 시작 성공"),
    })
    @PostMapping("/start")
    public ApiResponse<Boolean> startFirstGame() {
        pachinkoService.startFirstRound();
        return ApiResponse.onSuccess(SuccessCode.PACHINKO_START_SUCCESS, true);
    }

    @Operation(summary = "빠칭코 보상 알려주기", description = "빠칭코 게임 종료 후, 각 사용자가 받는 보상을 알려주는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PACHINKO_2003", description = "빠칭코 보상 반환 성공"),
    })
    @GetMapping("/reward")
    public ApiResponse<PachinkoRewardResDto> getRewards(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        List<Long> userJewelList = pachinkoService.getRewards(user);
        List<Pachinko> pachinkoList = pachinkoService.getPreviousPachinkoRewards(user.getPreviousPachinkoRound());
        return ApiResponse.onSuccess(SuccessCode.PACHINKO_REWARD_SHOW_SUCCESS,
                PachinkoConverter.pachinkoRewardResDto(userJewelList, pachinkoList));
    }

}
