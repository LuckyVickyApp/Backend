package LuckyVicky.backend.invitation.repository;

import LuckyVicky.backend.invitation.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long>{
    Optional<Invitation> findByWriter(String writerUsername);
}
