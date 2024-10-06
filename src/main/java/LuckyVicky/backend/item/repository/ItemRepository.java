package LuckyVicky.backend.item.repository;

import LuckyVicky.backend.item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // 상품명을 기반으로 상품 조회
    Optional<Item> findByName(String name);

    // 강화 가능한 상품 리스트 조회 (강화 시작일 <= 오늘 <= 강화 종료일)
    @Query("SELECT i FROM Item i WHERE i.enhanceStartDate <= :curDate AND :curDate <= :enhanceEndDate")
    List<Item> findItemsEligibleForEnhancement(@Param("curDate") LocalDate curDate, @Param("enhanceEndDate") LocalDate enhanceEndDate);
}
