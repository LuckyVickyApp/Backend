package LuckyVicky.backend.attendance.controller;

import LuckyVicky.backend.attendance.dto.AttendanceResponseDto.AttendanceRewardResDto;
import LuckyVicky.backend.attendance.service.AttendanceService;
import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "출석 보상", description = "사용자의 출석 보상 시스템 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "출석 체크", description = "사용자가 출석 체크를 하여 보상을 받을 수 있습니다. 하루에 한 번만 출석 가능합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ATTENDANCE_2001", description = "출석 처리 완료 및 보상 반환")
    })
    @PostMapping("/check-in")
    public ApiResponse<AttendanceRewardResDto> checkIn(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = attendanceService.findUserByUsername(customUserDetails.getUsername());
        AttendanceRewardResDto rewardDto = attendanceService.processAttendance(user);
        return ApiResponse.onSuccess(SuccessCode.ATTENDANCE_SUCCESS, rewardDto);
    }

    @Operation(summary = "출석 보상 목록 조회", description = "사용 가능한 모든 출석 보상 정보를 반환합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ATTENDANCE_2002", description = "출석 보상 목록 반환 완료")
    })
    @GetMapping("/rewards")
    public ApiResponse<List<AttendanceRewardResDto>> getAllAttendanceRewards() {
        List<AttendanceRewardResDto> rewards = attendanceService.getAllAttendanceRewards();
        return ApiResponse.onSuccess(SuccessCode.ATTENDANCE_REWARDS_SUCCESS, rewards);
    }
}
