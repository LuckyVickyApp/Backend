package LuckyVicky.backend.attendance.service;

import LuckyVicky.backend.attendance.converter.AttendanceConverter;
import LuckyVicky.backend.attendance.dto.AttendanceDto;
import LuckyVicky.backend.attendance.domain.AttendanceRecord;
import LuckyVicky.backend.attendance.repository.AttendanceRepository;
import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import LuckyVicky.backend.user.repository.UserRepository;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final UserJewelRepository userJewelRepository;
    private final AttendanceConverter attendanceConverter;

    private static final String[] REWARD_MESSAGES = {
            "B급 보석 1개", "B급 보석 1개", "B급 보석 1개",
            "A급 보석 1개", "B급 보석 2개", "B급 보석 2개",
            "B급 보석 2개", "A급 보석 2개", "B급 보석 3개",
            "B급 보석 3개", "B급 보석 3개", "A급 보석 3개"
    };

    private static final int[] REWARD_COUNTS = {
            1, 1, 1,
            1, 2, 2,
            2, 2, 3,
            3, 3, 3
    };

    private static final String[] JEWEL_TYPES = {
            "B", "B", "B",
            "A", "B", "B",
            "B", "A", "B",
            "B", "B", "A"
    };

    @Transactional
    public AttendanceDto.AttendanceRewardDto processAttendance(User user) {
        LocalDate today = LocalDate.now();

        if (attendanceRepository.findByUserAndAttendanceDate(user, today).isPresent()) {
            throw new GeneralException(ErrorCode.ATTENDANCE_ALREADY_CHECKED);
        }

        AttendanceRecord attendanceRecord = AttendanceRecord.builder()
                .user(user)
                .attendanceDate(today)
                .build();
        attendanceRepository.save(attendanceRecord);

        int attendanceDay = user.getAttendanceDate() % 12;
        String rewardMessage = REWARD_MESSAGES[attendanceDay];
        int jewelCount = REWARD_COUNTS[attendanceDay];
        String jewelType = JEWEL_TYPES[attendanceDay];

        addJewel(user, jewelType, jewelCount);
        user.setAttendanceDate(user.getAttendanceDate() + 1);
        userRepository.save(user);

        return attendanceConverter.convertToDto(rewardMessage, jewelCount);
    }

    private void addJewel(User user, String jewelType, int count) {
        UserJewel jewel = userJewelRepository.findFirstByUserAndJewelType(user, JewelType.valueOf(jewelType));
        if (jewel == null) {
            throw new GeneralException(ErrorCode.ENHANCE_JEWEL_NOT_FOUND);
        }
        jewel.increaseCount(count);
        userJewelRepository.save(jewel);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }
}
