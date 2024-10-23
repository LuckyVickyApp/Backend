package LuckyVicky.backend.invitation.controller;

import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import LuckyVicky.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.invitation.sevice.InvitationService;
import LuckyVicky.backend.invitation.dto.InvitationRequestDto;
@Tag(name = "초대", description = "초대 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/invitation")
public class InvitationController {

    private final UserService userService;
    private final InvitationService invitationService;

    @Operation(summary = "친구 초대 받기", description = "초대한 친구를 입력하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "2011", description = "초대 수락 완료")
    })
    @PostMapping(value = "/accept")
    public ApiResponse<String> acceptInvitation(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody InvitationRequestDto.InvitationReqDto invitationReqDto
    ){
        User user = userService.findByUserName(customUserDetails.getUsername());
        String owner = invitationService.acceptInvitation(user, invitationReqDto.getInvitationCode());

        return ApiResponse.onSuccess(SuccessCode.INVITE_ACCEPT_SUCCESS, owner);
    }
}
