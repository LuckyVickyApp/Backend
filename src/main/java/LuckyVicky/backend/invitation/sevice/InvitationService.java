package LuckyVicky.backend.invitation.sevice;

import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.invitation.domain.Invitation;
import LuckyVicky.backend.invitation.repository.InvitationRepository;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    public String acceptInvitation(User writer, String code){
        User owner = userRepository.findByInviteCode(code).orElseThrow(()
                -> new GeneralException(ErrorCode.INVITATION_NOT_FOUND));

        invitationRepository.save(Invitation.builder().user(owner).friendUsername(writer.getUsername()).build());

        // 초대한 친구에게 보상주기

        return owner.getNickname();
    }

}
