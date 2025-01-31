package LuckyVicky.backend.user.converter;

import LuckyVicky.backend.global.fcm.domain.UserDeviceToken;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.dto.JwtDto;
import LuckyVicky.backend.user.dto.UserRequestDto;
import LuckyVicky.backend.user.dto.UserResponseDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserConverter {
    public static User saveUser(UserRequestDto.UserReqDto userReqDto, String nick, String code) {

        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String todayDate = today.format(dateFormatter);

        return User.builder()
                .email(userReqDto.getEmail())
                .username(userReqDto.getUsername())
                .nickname(nick)
                .provider(userReqDto.getProvider())
                .signInDate(todayDate)
                .lastAttendanceCheckedDay(0)
                .inviteCode(code)
                .previousPachinkoRound(-1L)
                .rouletteAvailableTime(today)
                .advertiseTodayLeftNum(10)
                .build();
    }

    public static UserDeviceToken saveDeviceToken(User user, String deviceToken) {
        return UserDeviceToken.builder()
                .user(user)
                .deviceToken(deviceToken)
                .build();
    }

    public static JwtDto jwtDto(String access, String refresh, String signIn) {
        return JwtDto.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .signIn(signIn)
                .build();
    }

    public static UserResponseDto.UserDeliveryInformationResDto userDeliveryResDto(User user,
                                                                                   String decryptedPhoneNumber) {
        return UserResponseDto.UserDeliveryInformationResDto.builder()
                .recipientName(user.getRecipientName())
                .phoneNumber(decryptedPhoneNumber)
                .streetAddress(user.getStreetAddress())
                .detailedAddress(user.getDetailedAddress())
                .build();
    }

    public static UserResponseDto.UserInformationResDto userInformationResDto(User user) {
        return UserResponseDto.UserInformationResDto.builder()
                .email(user.getEmail())
                .signInDate(user.getSignInDate())
                .inviteCode(user.getInviteCode())
                .build();
    }

    public static UserResponseDto.UserMyPageResDto userMyPageResDto(User user) {
        return UserResponseDto.UserMyPageResDto.builder()
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }
}
