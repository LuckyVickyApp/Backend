package LuckyVicky.backend.item.repository;

import LuckyVicky.backend.item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // 상품명을 기반으로 상품 조회
    Optional<Item> findByName(String name);
}
