package LuckyVicky.backend.user.controller;

import LuckyVicky.backend.aes.service.AesDecryptService;
import LuckyVicky.backend.global.api_payload.ApiResponse;
import LuckyVicky.backend.global.api_payload.SuccessCode;
import LuckyVicky.backend.user.converter.UserConverter;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.dto.JwtDto;
import LuckyVicky.backend.user.dto.UserRequestDto.UserAddressReqDto;
import LuckyVicky.backend.user.dto.UserRequestDto.UserNicknameReqDto;
import LuckyVicky.backend.user.dto.UserResponseDto.UserDeliveryInformationResDto;
import LuckyVicky.backend.user.dto.UserResponseDto.UserInformationResDto;
import LuckyVicky.backend.user.dto.UserResponseDto.UserMyPageResDto;
import LuckyVicky.backend.user.jwt.CustomUserDetails;
import LuckyVicky.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "회원", description = "회원 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AesDecryptService aesDecryptService;

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
    @PostMapping(value = "/nickname/update")
    public ApiResponse<Boolean> nickname(
            @RequestBody UserNicknameReqDto nicknameReqDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        userService.saveNickname(nicknameReqDto, user);
        return ApiResponse.onSuccess(SuccessCode.USER_NICKNAME_SUCCESS, true);
    }

    @Operation(summary = "프로필 사진 추가 및 수정", description = "프로필 사진을 추가하거나 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "2007", description = "프로필 사진 추가/수정 완료")
    })
    @PutMapping(value = "/profile-image/update", consumes = {"multipart/form-data"})
    public ApiResponse<String> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart("profileImage") MultipartFile profileImage) throws IOException {
        User user = userService.findByUserName(customUserDetails.getUsername());
        userService.updateProfileImage(profileImage, user);
        return ApiResponse.onSuccess(SuccessCode.USER_INFO_UPDATE_SUCCESS, "프로필 이미지가 성공적으로 업로드 되었습니다");
    }

    @Operation(summary = "배송지 정보 저장 및 수정", description = "사용자의 배송지 정보를 저장합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "2008", description = "배송지 정보(수령자 이름, 주소, 전화번호)가 저장되었습니다."),
    })
    @PostMapping("/delivery-information/update")
    public ApiResponse<Boolean> saveDeliveryInformation(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                        @RequestBody UserAddressReqDto userAddressReqDto
    )
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        User user = userService.findByUserName(customUserDetails.getUsername());
        userService.updateDeliveryInformation(user, userAddressReqDto);
        return ApiResponse.onSuccess(SuccessCode.USER_DELIVERY_INFORMATION_UPDATE_SUCCESS, true);
    }

    @Operation(summary = "배송지 정보 조회", description = "사용자의 배송지 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "2009", description = "배송지 정보(수령자 이름, 주소, 전화번호)가 조회되었습니다."),
    })
    @GetMapping("/delivery-information")
    public ApiResponse<UserDeliveryInformationResDto> getDeliveryInformation(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws Exception {
        User user = userService.findByUserName(customUserDetails.getUsername());
        String decryptedPhoneNumber = aesDecryptService.decryptPhoneNumber(user.getPhoneNumber());
        return ApiResponse.onSuccess(SuccessCode.USER_DELIVERY_INFORMATION_VIEW_SUCCESS,
                UserConverter.userDeliveryResDto(user, decryptedPhoneNumber));
    }


    @Operation(summary = "회원 정보 조회", description = "현재 로그인한 회원의 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "2006", description = "회원 정보 조회 완료")
    })
    @GetMapping("/info")
    public ApiResponse<UserInformationResDto> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        return ApiResponse.onSuccess(SuccessCode.USER_INFO_VIEW_SUCCESS, UserConverter.userInformationResDto(user));
    }

    @Operation(summary = "마이페이지 정보 조회", description = "현재 로그인한 회원의 닉네임과 사진을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "2010", description = "마이페이지 정보 조회 완료")
    })
    @GetMapping("/mypage")
    public ApiResponse<UserMyPageResDto> getUserMypage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findByUserName(customUserDetails.getUsername());
        return ApiResponse.onSuccess(SuccessCode.USER_MYPAGE_VIEW_SUCCESS, UserConverter.userMyPageResDto(user));
    }
}
