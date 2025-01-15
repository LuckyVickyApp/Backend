package LuckyVicky.backend.enhance.repository;

import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnhanceItemRepository extends JpaRepository<EnhanceItem, Long> {

    Optional<EnhanceItem> findByUserAndItem(User user, Item item);

    @Query("SELECT e FROM EnhanceItem e JOIN FETCH e.user WHERE e.item = :item ORDER BY e.enhanceLevel DESC, e.enhanceLevelReachedAt ASC")
    List<EnhanceItem> findEnhanceItemsByItemOrderByEnhanceLevelAndReachedTime(@Param("item") Item item);

    Integer countByItem(Item item);
}