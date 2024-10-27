package LuckyVicky.backend.attendance.converter;

import LuckyVicky.backend.attendance.dto.AttendanceDto;
import org.springframework.stereotype.Component;

@Component
public class AttendanceConverter {

    public AttendanceDto.AttendanceRewardDto convertToDto(String message, int jewelCount) {
        return new AttendanceDto.AttendanceRewardDto(message, jewelCount);
    }
}
