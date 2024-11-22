package LuckyVicky.backend.attendance.service;

import LuckyVicky.backend.attendance.converter.AttendanceConverter;
import LuckyVicky.backend.attendance.domain.AttendanceReward;
import LuckyVicky.backend.attendance.dto.AttendanceResponseDto.AttendanceRewardResDto;
import LuckyVicky.backend.attendance.repository.AttendanceRewardRepository;
import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import LuckyVicky.backend.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final UserRepository userRepository;
    private final UserJewelRepository userJewelRepository;
    private final AttendanceRewardRepository attendanceRewardRepository;

    @Transactional
    public AttendanceRewardResDto processAttendance(User user) {
        LocalDate today = LocalDate.now();

        // 오늘 이미 출석 체크를 했는지 확인
        if (user.getLastAttendanceDate() != null && user.getLastAttendanceDate().isEqual(today)) {
            throw new GeneralException(ErrorCode.ATTENDANCE_ALREADY_CHECKED);
        }

        int attendanceDay = user.getAttendanceDate() % 12 + 1;
        AttendanceReward reward = attendanceRewardRepository.findByDay(attendanceDay)
                .orElseThrow(() -> new GeneralException(ErrorCode.ATTENDANCE_REWARD_NOT_FOUND));

        // 보석 추가
        addJewel(user, reward.getJewelType(), reward.getJewelCount());

        // 출석 증가 및 마지막 출석 날짜 업데이트
        user.incrementAttendance();
        userRepository.save(user);

        // Converter를 사용하여 DTO 반환
        return AttendanceConverter.convertToDto(reward);
    }

    private void addJewel(User user, JewelType jewelType, int count) {
        UserJewel jewel = userJewelRepository.findFirstByUserAndJewelType(user, jewelType);
        if (jewel == null) {
            throw new GeneralException(ErrorCode.USER_JEWEL_NOT_FOUND);
        }
        jewel.increaseCount(count);
        userJewelRepository.save(jewel);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRewardResDto> getAllAttendanceRewards() {
        List<AttendanceReward> rewards = attendanceRewardRepository.findAll();

        return rewards.stream()
                .map(AttendanceConverter::convertToDto)
                .toList();
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }
}
