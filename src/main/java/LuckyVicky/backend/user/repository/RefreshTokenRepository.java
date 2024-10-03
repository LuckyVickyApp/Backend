package LuckyVicky.backend.user.repository;

import LuckyVicky.backend.user.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
}
