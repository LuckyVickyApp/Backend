package LuckyVicky.backend.invitation.sevice;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.global.fcm.converter.FcmConverter;
import LuckyVicky.backend.global.fcm.domain.UserDeviceToken;
import LuckyVicky.backend.global.fcm.dto.FcmRequestDto.FcmSimpleReqDto;
import LuckyVicky.backend.global.fcm.service.FcmService;
import LuckyVicky.backend.global.util.Constant;
import LuckyVicky.backend.invitation.domain.Invitation;
import LuckyVicky.backend.invitation.repository.InvitationRepository;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import LuckyVicky.backend.user.repository.UserRepository;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
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
    private final FcmService fcmService;

    public String acceptInvitation(User writer, String code) throws IOException {
        User owner = userRepository.findByInviteCode(code).orElseThrow(()
                -> new GeneralException(ErrorCode.INVITATION_NOT_FOUND));

        if (Objects.equals(owner.getInviteCode(), code)) {
            throw new GeneralException(ErrorCode.INVITATION_MINE_INVALID);
        }

        if (invitationRepository.findByWriter(writer.getUsername()).isPresent()) {
            throw new GeneralException(ErrorCode.INVITATION_ALREADY_ACCEPTED);
        }

        invitationRepository.save(Invitation.builder().owner(owner).writer(writer.getUsername()).build());

        UserJewel ownerSLevelJewel = jewelRepository.findFirstByUserAndJewelType(owner, JewelType.S);
        ownerSLevelJewel.increaseCount(1);
        System.out.println(ownerSLevelJewel.getCount());

        List<UserDeviceToken> userDeviceTokens = owner.getDeviceTokenList();
        for (UserDeviceToken userDeviceToken : userDeviceTokens) {
            FcmSimpleReqDto fcmSimpleReqDto = FcmConverter.toFcmSimpleReqDto(userDeviceToken.getDeviceToken(),
                    Constant.FCM_INVITATION_TITLE, createFcmBody(writer.getNickname(), owner.getNickname()));
            fcmService.sendMessageTo(fcmSimpleReqDto);
        }

        return owner.getNickname();
    }

    private String createFcmBody(String writer, String owner) {
        return writer + "님이 " + owner + "님의 친구 코드를 입력하셨습니다!";
    }
}
