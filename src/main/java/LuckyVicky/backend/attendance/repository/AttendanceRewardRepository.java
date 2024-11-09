package LuckyVicky.backend.attendance.repository;

import LuckyVicky.backend.attendance.domain.AttendanceReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceRewardRepository extends JpaRepository<AttendanceReward, Long> {
    Optional<AttendanceReward> findByDay(int day);
}
