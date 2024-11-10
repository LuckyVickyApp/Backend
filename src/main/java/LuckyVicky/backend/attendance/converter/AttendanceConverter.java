package LuckyVicky.backend.attendance.converter;

import LuckyVicky.backend.attendance.dto.AttendanceResponseDto.AttendanceRewardResDto;

public class AttendanceConverter {
    private AttendanceConverter() {
        throw new UnsupportedOperationException("Converter class는 인스턴스화가 불가능합니다.");
    }

    public AttendanceRewardResDto convertToDto(String rewardMessage, int jewelCount) {
        return AttendanceRewardResDto.builder()
                .rewardMessage(rewardMessage)
                .jewelCount(jewelCount)
                .build();
    }
}
