package LuckyVicky.backend.roulette.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.roulette.converter.RouletteConverter;
import LuckyVicky.backend.roulette.dto.RouletteResultDto;
import LuckyVicky.backend.roulette.service.RouletteService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "룰렛", description = "룰렛 기능 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/roulette")
public class RouletteController {

    private final RouletteService rouletteService;
    private final RouletteConverter rouletteConverter;

    @Operation(summary = "룰렛 돌리기", description = "사용자가 룰렛을 돌려 보석을 얻을 수 있습니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ROULETTE_2001", description = "룰렛 결과 반환")
    })
    @GetMapping("/spin")
    public ApiResponse<RouletteResultDto> spinRoulette(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 사용자 정보 가져오기
        User user = rouletteService.findUserByUsername(customUserDetails.getUsername());
        RouletteResultDto resultDto = rouletteService.spinRoulette(user);
        RouletteResultDto convertedResult = rouletteConverter.convertToDto(resultDto.getMessage(), resultDto.getJewelCount());
        return ApiResponse.onSuccess(SuccessCode.ROULETTE_SUCCESS, convertedResult);
    }
}
