package LuckyVicky.backend.user.controller;

import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.user.converter.UserConverter;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.dto.JwtDto;
import LuckyVicky.backend.user.dto.UserRequestDto;
import LuckyVicky.backend.user.dto.UserResponseDto;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import LuckyVicky.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import java.io.IOException;

@Tag(name = "회원", description = "회원 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "로그아웃", description = "로그아웃하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2001", description = "로그아웃 되었습니다."),
    })
    @DeleteMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return ApiResponse.onSuccess(SuccessCode.USER_LOGOUT_SUCCESS, "refresh token 삭제 완료");
    }

    @Operation(summary = "토큰 재발급", description = "토큰을 재발급하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2002", description = "토큰 재발급이 완료되었습니다."),
    })
    @PostMapping("/reissue")
    public ApiResponse<JwtDto> reissue(
            HttpServletRequest request
    ) {
        JwtDto jwt = userService.reissue(request);
        return ApiResponse.onSuccess(SuccessCode.USER_REISSUE_SUCCESS, jwt);
    }

    @Operation(summary = "회원탈퇴", description = "회원 탈퇴하는 메서드입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2003", description = "회원탈퇴가 완료되었습니다."),
    })
    @DeleteMapping("/delete")
    public ApiResponse<String> deleteUser(Authentication auth) {
        userService.deleteUser(auth.getName());
        return ApiResponse.onSuccess(SuccessCode.USER_DELETE_SUCCESS, "user entity 삭제 완료");
    }

    @Operation(summary = "닉네임 입력, 수정", description = "중복 안되는 닉네임을 입력받는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_2004", description = "닉네임 생성이 완료되었습니다.")
    })
    @PostMapping(value = "/nickname")
    public ApiResponse<Boolean> nickname(
            @RequestBody UserRequestDto.UserNicknameReqDto nicknameReqDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        userService.saveNickname(nicknameReqDto, user);

        return ApiResponse.onSuccess(SuccessCode.USER_NICKNAME_SUCCESS, true);
    }

    @Operation(summary = "주소 저장 및 수정", description = "사용자의 주소를 저장합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주소가 저장되었습니다."),
    })
    @PostMapping("/address")
    public ApiResponse<String> saveAddress(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @RequestBody UserRequestDto.UserAddressDto userUpdateAddressDto) {
        try {
            User user = userService.findByUserName(customUserDetails.getUsername());
            userService.updateAddress(user, userUpdateAddressDto);
            return ApiResponse.onSuccess(SuccessCode.USER_INFO_UPDATE_SUCCESS, "주소가 성공적으로 저장되었습니다.");
        } catch (IllegalArgumentException e) {
            throw GeneralException.of(ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            throw GeneralException.of(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
