package LuckyVicky.backend.display_board.controller;

import LuckyVicky.backend.display_board.service.DisplayBoardService;
import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "전광판")
@RestController
@RequestMapping("/display-board")
@RequiredArgsConstructor
public class DisplayBoardController {
    private final DisplayBoardService displayBoardService;

    @Operation(summary = "전광판 큐 리셋 메서드", description = "전광판 큐 내 메세지들을 리셋하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DISPLAY_2001", description = "전광판 큐 내 메세지들이 리셋되었습니다.")
    })
    @GetMapping("/reset")
    public ApiResponse<Integer> resetDisplayBoard() {
        return ApiResponse.onSuccess(SuccessCode.DISPLAY_BOARD_RESET_QUEUE_SUCCESS, displayBoardService.initializeQueue());
    }
}
