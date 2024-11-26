package LuckyVicky.backend.global.fcm.repository;

import LuckyVicky.backend.global.fcm.domain.UserDeviceToken;
import LuckyVicky.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    UserDeviceToken findTopByUserOrderByCreatedAtAsc(User user); // 가장 오래된

    UserDeviceToken findTopByUserOrderByCreatedAtDesc(User user); // 가장 최신의
}
