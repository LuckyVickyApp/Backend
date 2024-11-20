package LuckyVicky.backend.attendance.converter;

import LuckyVicky.backend.attendance.dto.AttendanceResponseDto.AttendanceRewardResDto;
import LuckyVicky.backend.attendance.domain.AttendanceReward;
import org.springframework.stereotype.Component;

@Component
public class AttendanceConverter {

    public AttendanceRewardResDto convertToDto(AttendanceReward reward) {
        return AttendanceRewardResDto.builder()
                .day(reward.getDay())
                .jewelType(reward.getJewelType())
                .jewelCount(reward.getJewelCount())
                .rewardMessage(reward.getRewardMessage())
                .build();
    }
}
