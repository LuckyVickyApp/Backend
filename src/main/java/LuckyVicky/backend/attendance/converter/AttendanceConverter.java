package LuckyVicky.backend.attendance.converter;

import static LuckyVicky.backend.global.util.Constant.CONVERTER_INSTANTIATION_NOT_ALLOWED;

import LuckyVicky.backend.attendance.dto.AttendanceResponseDto.AttendanceRewardResDto;

public class AttendanceConverter {
    private AttendanceConverter() {
        throw new UnsupportedOperationException(CONVERTER_INSTANTIATION_NOT_ALLOWED);
    }

    public AttendanceRewardResDto convertToDto(String rewardMessage, int jewelCount) {
        return AttendanceRewardResDto.builder()
                .rewardMessage(rewardMessage)
                .jewelCount(jewelCount)
                .build();
    }
}
