package LuckyVicky.backend.attendance.repository;

import LuckyVicky.backend.attendance.domain.AttendanceRecord;
import LuckyVicky.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    //특정 날짜 출석 여부 판단함
    Optional<AttendanceRecord> findByUserAndAttendanceDate(User user, LocalDate attendanceDate);
}
