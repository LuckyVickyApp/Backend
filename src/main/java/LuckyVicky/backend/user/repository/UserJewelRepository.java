package LuckyVicky.backend.user.repository;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJewelRepository extends JpaRepository<UserJewel, Long> {
    UserJewel findFirstByUserAndJewelType(User user, JewelType jewelType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserJewel> findByUserAndJewelType(User user, JewelType jewelType);
}
