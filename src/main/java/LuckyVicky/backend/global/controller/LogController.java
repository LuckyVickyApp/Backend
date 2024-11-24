package LuckyVicky.backend.global.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.global.s3.S3LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그", description = "로그 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
public class LogController {

    private final S3LogService logService;

    @Operation(summary = "Error Log", description = "로컬 서버에 있는 에러 로그를 S3에 업로드 합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LOG_2001", description = "에러 로그 S3 업로드 완료")
    })
    @PostMapping("/error-upload")
    public ApiResponse<String> uploadLogFile() {

        logService.uploadOrAppendLog("today", true);
        return ApiResponse.onSuccess(SuccessCode.ERROR_LOG_S3_UPLOADED, "Log file uploaded to S3.");
    }
}
