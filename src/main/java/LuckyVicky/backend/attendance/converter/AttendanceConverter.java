package LuckyVicky.backend.attendance.converter;

import LuckyVicky.backend.attendance.dto.AttendanceResponseDto.AttendanceRewardResDto;
import org.springframework.stereotype.Component;

@Component
public class AttendanceConverter {
    public AttendanceRewardResDto convertToDto(String rewardMessage, int jewelCount) {
        return AttendanceRewardResDto.builder()
                .rewardMessage(rewardMessage)
                .jewelCount(jewelCount)
                .build();
    }
}
