package LuckyVicky.backend.invitation.sevice;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.invitation.domain.Invitation;
import LuckyVicky.backend.invitation.repository.InvitationRepository;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import LuckyVicky.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserJewelRepository jewelRepository;
    private final UserRepository userRepository;

    public String acceptInvitation(User writer, String code){
        User owner = userRepository.findByInviteCode(code).orElseThrow(()
                -> new GeneralException(ErrorCode.INVITATION_NOT_FOUND));

        // 이미 초대 수락 했다면, 또 못하도록
        if (invitationRepository.findByWriter(writer.getUsername()).isPresent())
            throw new GeneralException(ErrorCode.INVITATION_ALREADY_ACCEPTED);

        invitationRepository.save(Invitation.builder().owner(owner).writer(writer.getUsername()).build());

        // 초대자는 s급 보석 하나 얻도록
        UserJewel sLevelJewel = jewelRepository.findFirstByUserAndJewelType(owner, JewelType.S);
        sLevelJewel.updateCount(1);
        System.out.println(sLevelJewel.getCount());

        return owner.getNickname();
    }

}
