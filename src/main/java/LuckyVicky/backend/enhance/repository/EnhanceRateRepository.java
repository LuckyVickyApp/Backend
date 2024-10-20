package LuckyVicky.backend.enhance.repository;

import LuckyVicky.backend.enhance.domain.EnhanceRate;
import LuckyVicky.backend.enhance.domain.JewelType;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface EnhanceRateRepository extends JpaRepository<EnhanceRate, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EnhanceRate> findByEnhanceLevelAndJewelType(Integer enhanceLevel, JewelType jewelType);
}
