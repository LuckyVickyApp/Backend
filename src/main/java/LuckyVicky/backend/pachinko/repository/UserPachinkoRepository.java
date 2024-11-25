package LuckyVicky.backend.pachinko.repository;

import LuckyVicky.backend.pachinko.domain.UserPachinko;
import LuckyVicky.backend.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPachinkoRepository extends JpaRepository<UserPachinko, Long> {
    Optional<UserPachinko> findByUserAndRound(User user, Long round);

    List<UserPachinko> findByRound(Long round);

    @Query("SELECT COALESCE(MAX(u.round), 0) FROM UserPachinko u")
        // Null인 경우 0 반환
    Long findCurrentRound();

}
