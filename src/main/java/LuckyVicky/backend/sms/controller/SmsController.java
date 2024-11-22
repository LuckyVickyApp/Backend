package LuckyVicky.backend.sms.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.sms.service.SmsService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import LuckyVicky.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "문자 인증", description = "문자 인증 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;
    private final UserService userService;

    @Operation(summary = "문자 인증 번호 전송", description = "인증 문자를 보내는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "2001", description = "문자 인증 번호 전송 성공")
    })
    @PostMapping("/send-certificate")
    public ApiResponse<String> sendVerificationCode(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(name = "phoneNumber") String phoneNumber
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        String code = smsService.sendVerificationCode(phoneNumber);
        return ApiResponse.onSuccess(SuccessCode.SMS_CERTIFICATE_SEND_SUCCESS, code);
    }

    @Operation(summary = "인증 번호 유효 판단", description = "입력한 인증 번호가 옳은지 판단하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "2002", description = "문자 인증 성공")
    })
    @GetMapping("/verify")
    public ApiResponse<String> verifyCode(
            @RequestParam(name = "inputCode") String inputCode,
            @RequestParam(name = "correctCode") String correctCode
    ) {
        smsService.verifyCode(inputCode, correctCode);
        return ApiResponse.onSuccess(SuccessCode.SMS_CERTIFICATE_SUCCESS, "Success");
    }
}

