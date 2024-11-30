package LuckyVicky.backend.user.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.global.fcm.service.UserDeviceTokenService;
import LuckyVicky.backend.user.converter.UserConverter;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.dto.JwtDto;
import LuckyVicky.backend.user.dto.UserRequestDto;
import LuckyVicky.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "토큰", description = "access token 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController {

    private final UserService userService;
    private final UserDeviceTokenService userDeviceTokenService;

    @Operation(summary = "토큰 반환", description = "프론트에게 유저 정보 받아 토큰 반환하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2011", description = "회원가입 & 로그인 성공"),
    })
    @PostMapping("/generate")
    public ApiResponse<JwtDto> tokenToFront(
            @RequestBody UserRequestDto.UserReqDto userReqDto // email, username, provider
    ) {
        Boolean isMember = userService.checkMemberByEmail(userReqDto.getEmail());

        String accessToken = "";
        String refreshToken = "";

        String signIn = "wasUser";

        if (isMember) {
            User user = userService.findByUserName(userReqDto.getUsername());

            userDeviceTokenService.registerDeviceIfPossible(user, userReqDto.getDeviceToken());

            JwtDto jwt = userService.jwtMakeSave(userReqDto.getUsername());
            accessToken = jwt.getAccessToken();
            refreshToken = jwt.getRefreshToken();

        } else {
            User newUser = userService.createUser(userReqDto);

            userService.registerDeviceToken(newUser, userReqDto.getDeviceToken());

            userService.makeUserJewel(newUser);

            JwtDto jwt = userService.jwtMakeSave(userReqDto.getUsername());
            accessToken = jwt.getAccessToken();
            refreshToken = jwt.getRefreshToken();

            signIn = "newUser";
        }

        return ApiResponse.onSuccess(SuccessCode.USER_LOGIN_SUCCESS,
                UserConverter.jwtDto(accessToken, refreshToken, signIn));
    }
}
