package LuckyVicky.backend.item.repository;

import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.domain.ItemLike;
import LuckyVicky.backend.user.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ItemLikeRepository extends JpaRepository<ItemLike, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ItemLike> findByUserAndItem(User user, Item item);

}
