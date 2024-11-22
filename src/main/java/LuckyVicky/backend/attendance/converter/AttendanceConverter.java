package LuckyVicky.backend.attendance.converter;

import static LuckyVicky.backend.global.util.Constant.CONVERTER_INSTANTIATION_NOT_ALLOWED;

import LuckyVicky.backend.attendance.domain.AttendanceReward;
import LuckyVicky.backend.attendance.dto.AttendanceResponseDto.AttendanceRewardResDto;

public class AttendanceConverter {

    private AttendanceConverter() {
        throw new UnsupportedOperationException(CONVERTER_INSTANTIATION_NOT_ALLOWED);
    }

    public static AttendanceRewardResDto convertToDto(AttendanceReward reward) {
        return AttendanceRewardResDto.builder()
                .day(reward.getDay()) // 출석 일차
                .jewelType(reward.getJewelType()) // 보석 종류(enum)
                .jewelCount(reward.getJewelCount()) // 보석 개수
                .build();
    }
}
