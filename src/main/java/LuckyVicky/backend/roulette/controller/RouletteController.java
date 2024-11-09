package LuckyVicky.backend.roulette.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.roulette.dto.RouletteResponseDto;
import LuckyVicky.backend.roulette.service.RouletteService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import LuckyVicky.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "룰렛", description = "룰렛 기능 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/roulette")
public class RouletteController {

    private final RouletteService rouletteService;
    private final UserService userService;

    @Operation(summary = "룰렛 가능 여부 확인", description = "사용자가 룰렛을 돌릴 수 있는지 여부를 반환합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ROULETTE_2002", description = "룰렛 가능 여부 반환")
    })
    @GetMapping("/availability")
    public ApiResponse<RouletteResponseDto.RouletteAvailableDto> checkRouletteAvailability(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        RouletteResponseDto.RouletteAvailableDto availability = rouletteService.checkRouletteAvailability(user);

        // 메시지를 반환하지 않고 DTO만 반환
        return ApiResponse.onSuccess(SuccessCode.ROULETTE_AVAILABILITY_SUCCESS, availability);
    }

    @Operation(summary = "룰렛 결과 저장", description = "프론트엔드에서 받은 룰렛 결과를 서버에 저장합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ROULETTE_2001", description = "룰렛 결과 저장 성공")
    })
    @PostMapping("/result")
    public ApiResponse<String> saveRouletteResult(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("jewelType") String jewelType,
            @RequestParam("jewelCount") int jewelCount
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        rouletteService.saveRouletteResult(user, jewelType, jewelCount);
        return ApiResponse.onSuccess(SuccessCode.ROULETTE_SUCCESS, "ok");
    }
}
