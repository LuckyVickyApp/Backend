package LuckyVicky.backend.attendance.converter;

import LuckyVicky.backend.attendance.dto.AttendanceResponseDto.AttendanceRewardResDto;
import LuckyVicky.backend.attendance.domain.AttendanceReward;
import org.springframework.stereotype.Component;

@Component
public class AttendanceConverter {

    /**
     * AttendanceReward를 AttendanceRewardResDto로 변환
     */
    public AttendanceRewardResDto convertToDto(AttendanceReward reward) {
        return AttendanceRewardResDto.builder()
                .day(reward.getDay()) // 출석 일차
                .jewelType(reward.getJewelType()) // 보석 종류(enum)
                .jewelCount(reward.getJewelCount()) // 보석 개수
                .build();
    }
}
