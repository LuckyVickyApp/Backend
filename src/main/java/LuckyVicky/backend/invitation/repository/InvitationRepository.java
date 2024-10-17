package LuckyVicky.backend.invitation.repository;

import LuckyVicky.backend.invitation.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long>{

}
