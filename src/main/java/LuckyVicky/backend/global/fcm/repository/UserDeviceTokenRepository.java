package LuckyVicky.backend.global.fcm.repository;

import LuckyVicky.backend.global.fcm.domain.UserDeviceToken;
import LuckyVicky.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    UserDeviceToken findTopByUserOrderByCreatedAtAsc(User user);
}
