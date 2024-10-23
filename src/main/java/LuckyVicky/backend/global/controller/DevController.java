package LuckyVicky.backend.global.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.pachinko.service.PachinkoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "dev", description = "dev api - 관리자 api")

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
public class DevController {

    private final PachinkoService pachinkoService;

    @Operation(summary = "빠칭코 set update", description = "빠칭코에서 선택된 칸들을 update하는 메서드 입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PACHINKO_2011", description = "빠칭코 선택완료 칸 확인 성공"),
    })
    @PostMapping("/pachinko/set-update")
    public ApiResponse<Boolean> updateSet(){
        pachinkoService.updateSelectedSquaresSet();
        return ApiResponse.onSuccess(SuccessCode.OK, true);
    }

}
