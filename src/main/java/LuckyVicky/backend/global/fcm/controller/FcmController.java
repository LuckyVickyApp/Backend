package LuckyVicky.backend.global.fcm.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.global.fcm.dto.FcmRequestDto.FcmSimpleReqDto;
import LuckyVicky.backend.global.fcm.service.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FCM", description = "FCM 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {
    private final FcmService fcmService;

    @Operation(summary = "fcm 알림 전송", description = "fcm 전송을 테스트해볼 수 있는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FCM_2001", description = "fcm 알림 전송 성공"),
    })
    @PostMapping("/pushMessage")
    public ApiResponse<String> pushMessage(@RequestBody FcmSimpleReqDto requestDTO) throws IOException {
        System.out.println(requestDTO.getDeviceToken() + " "
                + requestDTO.getTitle() + " " + requestDTO.getBody());
        fcmService.sendMessageTo(requestDTO);
        return ApiResponse.onSuccess(SuccessCode.FCM_SEND_SUCCESS, "fcm 알람이 성공적으로 전송되었습니다.");
    }
}
