package LuckyVicky.backend.pachinko.repository;

import LuckyVicky.backend.pachinko.domain.Pachinko;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PachinkoRepository extends JpaRepository<Pachinko, Long> {
    Optional<Pachinko> findByRoundAndSquare(Long round, Integer square);
}
