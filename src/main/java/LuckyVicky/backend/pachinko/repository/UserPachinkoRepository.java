package LuckyVicky.backend.pachinko.repository;

import LuckyVicky.backend.pachinko.domain.UserPachinko;
import LuckyVicky.backend.user.domain.User;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPachinkoRepository extends JpaRepository<UserPachinko, Long> {

    Optional<UserPachinko> findByUserAndRound(User user, Long round);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UserPachinko u WHERE u.user = :user AND u.round = :round")
    Optional<UserPachinko> findByUserAndRoundForUpdate(@Param("user") User user, @Param("round") Long round);

    List<UserPachinko> findByRound(Long round);

    @Query("SELECT COALESCE(MAX(u.round), 0) FROM UserPachinko u")
    Long findCurrentRound();

}
