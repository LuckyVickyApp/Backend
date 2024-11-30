package LuckyVicky.backend.display_board.repository;

import LuckyVicky.backend.display_board.domain.DisplayMessage;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DisplayMessageRepository extends JpaRepository<DisplayMessage, Long> {
    @Query("SELECT d FROM DisplayMessage d WHERE d.displayStartTime <= :now AND :now <= d.displayEndTime")
    List<DisplayMessage> findActiveDisplayMessages(@Param("now")LocalDateTime now);
}
