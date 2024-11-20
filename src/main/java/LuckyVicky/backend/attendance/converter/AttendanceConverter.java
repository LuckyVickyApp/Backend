package LuckyVicky.backend.attendance.converter;

import LuckyVicky.backend.attendance.dto.AttendanceResponseDto.AttendanceRewardResDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AttendanceConverter {
/*    private AttendanceConverter() {
        throw new UnsupportedOperationException(CONVERTER_INSTANTIATION_NOT_ALLOWED);
    }*/

    public AttendanceRewardResDto convertToDto(String rewardMessage, int jewelCount) {
        return AttendanceRewardResDto.builder()
                .rewardMessage(rewardMessage)
                .jewelCount(jewelCount)
                .build();
    }
}
