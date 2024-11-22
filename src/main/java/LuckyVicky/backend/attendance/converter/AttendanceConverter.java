package LuckyVicky.backend.attendance.converter;

import static LuckyVicky.backend.global.util.Constant.CONVERTER_INSTANTIATION_NOT_ALLOWED;

import LuckyVicky.backend.attendance.domain.AttendanceReward;
import LuckyVicky.backend.attendance.dto.AttendanceResponseDto.AttendanceRewardResDto;
import LuckyVicky.backend.attendance.dto.AttendanceResponseDto.AttendanceRewardResponseDto;
import java.util.List;

public class AttendanceConverter {

    private AttendanceConverter() {
        throw new UnsupportedOperationException(CONVERTER_INSTANTIATION_NOT_ALLOWED);
    }

    public static AttendanceRewardResDto convertToDto(AttendanceReward reward) {
        return AttendanceRewardResDto.builder()
                .day(reward.getDay())
                .jewelType(reward.getJewelType())
                .jewelCount(reward.getJewelCount())
                .build();
    }

    public static AttendanceRewardResponseDto attendanceRewardResponseDto(List<AttendanceRewardResDto> rewards,
                                                                          int lastAttendanceDay) {
        return AttendanceRewardResponseDto.builder()
                .lastAttendanceCheckedDay(lastAttendanceDay)
                .attendanceRewardResDtoList(rewards)
                .build();
    }
}
