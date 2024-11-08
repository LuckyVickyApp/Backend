package LuckyVicky.backend.attendance.service;

import LuckyVicky.backend.attendance.dto.AttendanceResponseDto;
import LuckyVicky.backend.attendance.domain.AttendanceReward;
import LuckyVicky.backend.attendance.repository.AttendanceRewardRepository;
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

    private final UserRepository userRepository;
    private final UserJewelRepository userJewelRepository;
    private final AttendanceRewardRepository attendanceRewardRepository;

    @Transactional
    public AttendanceResponseDto.AttendanceRewardResDto processAttendance(User user) {
        LocalDate today = LocalDate.now();

        if (user.getLastAttendanceDate() != null && user.getLastAttendanceDate().isEqual(today)) {
            throw new GeneralException(ErrorCode.ATTENDANCE_ALREADY_CHECKED);
        }

        int attendanceDay = user.getAttendanceDate() % 12 + 1;
        AttendanceReward reward = attendanceRewardRepository.findByDay(attendanceDay)
                .orElseThrow(() -> new GeneralException(ErrorCode.ATTENDANCE_REWARD_NOT_FOUND));

        addJewel(user, reward.getJewelType(), reward.getJewelCount());

        user.incrementAttendance();
        userRepository.save(user);

        return AttendanceResponseDto.AttendanceRewardResDto.builder()
                .rewardMessage(reward.getRewardMessage())
                .jewelCount(reward.getJewelCount())
                .build();
    }

    private void addJewel(User user, JewelType jewelType, int count) {
        UserJewel jewel = userJewelRepository.findFirstByUserAndJewelType(user, jewelType);
        if (jewel == null) {
            throw new GeneralException(ErrorCode.USER_JEWEL_NOT_FOUND);
        }
        jewel.increaseCount(count);
        userJewelRepository.save(jewel);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }
}
