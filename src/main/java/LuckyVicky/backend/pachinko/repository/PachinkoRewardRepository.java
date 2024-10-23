package LuckyVicky.backend.pachinko.repository;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.pachinko.domain.PachinkoReward;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PachinkoRewardRepository extends JpaRepository<PachinkoReward, Long> {
    Optional<PachinkoReward> findByJewelTypeAndJewelNum(JewelType jewelType, int jewelNum);
}
