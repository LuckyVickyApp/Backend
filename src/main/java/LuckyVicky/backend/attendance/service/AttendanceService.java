package LuckyVicky.backend.attendance.service;

import static LuckyVicky.backend.global.util.Constant.ATTENDANCE_CYCLE_DAYS;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final UserRepository userRepository;
    private final UserJewelRepository userJewelRepository;
    private final AttendanceRewardRepository attendanceRewardRepository;

    @Transactional
    public AttendanceRewardResDto processAttendance(User user) {
        LocalDate today = LocalDate.now();

        validateAttendanceEligibility(today, user);

        int attendanceDay = calculateAttendanceDay(user);

        AttendanceReward reward = getAttendanceReward(attendanceDay, user);

        updateAttendance(today, user);

        return AttendanceConverter.convertToDto(reward);
    }

    private void validateAttendanceEligibility(LocalDate today, User user) {
        if (user.getLastAttendanceDate().isEqual(today)) {
            throw new GeneralException(ErrorCode.ATTENDANCE_ALREADY_CHECKED);
        }
    }

    private int calculateAttendanceDay(User user) {
        return (user.getLastAttendanceCheckedDay() % ATTENDANCE_CYCLE_DAYS) + 1;
    }

    private AttendanceReward getAttendanceReward(int attendanceDay, User user) {
        AttendanceReward reward = attendanceRewardRepository.findByDay(attendanceDay)
                .orElseThrow(() -> new GeneralException(ErrorCode.ATTENDANCE_REWARD_NOT_FOUND));

        updateUserJewel(user, reward.getJewelType(), reward.getJewelCount());

        return reward;
    }

    private void updateAttendance(LocalDate today, User user) {
        user.updateUserAttendance(today);
        userRepository.save(user);
    }

    private void updateUserJewel(User user, JewelType jewelType, int count) {
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
