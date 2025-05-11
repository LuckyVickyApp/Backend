package LuckyVicky.backend.pachinko.repository;

import LuckyVicky.backend.pachinko.domain.UserPachinko;
import LuckyVicky.backend.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPachinkoRepository extends JpaRepository<UserPachinko, Long> {

    List<UserPachinko> findByUserAndRound(User user, Long round);

    long countByUserAndRound(User user, Long round);

    boolean existsByUserAndRoundAndSquare(User user, Long round, Integer square);

    List<UserPachinko> findByRound(Long round);

    @Query("SELECT COALESCE(MAX(u.round), 0) FROM UserPachinko u")
    Long findCurrentRound();

}
