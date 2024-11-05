package LuckyVicky.backend.user.converter;

import LuckyVicky.backend.global.entity.Uuid;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.dto.JwtDto;
import LuckyVicky.backend.user.dto.UserRequestDto;
import LuckyVicky.backend.user.dto.UserResponseDto;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public class UserConverter {
    public static User saveUser(UserRequestDto.UserReqDto userReqDto, String nick) {

        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String todayDate = today.format(dateFormatter);

        return User.builder()
                .email(userReqDto.getEmail())
                .username(userReqDto.getUsername())
                .nickname(nick)
                .provider(userReqDto.getProvider())
                .signInDate(todayDate)
                .attendanceDate(0)
                .inviteCode(Uuid.generateUuid().getUuid())
                .previousPachinkoRound(0L)
                .rouletteAvailableTime(today)
                .advertiseTodayLeftNum(10)
                .build();
    }

    public static JwtDto jwtDto(String access, String refresh, String signIn) {
        return JwtDto.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .signIn(signIn)
                .build();
    }

    public static UserResponseDto.MyPageUserDto toUserDTO(User user) {
        return UserResponseDto.MyPageUserDto.builder()
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .address(user.getAddress())
                .signInDate(user.getSignInDate())
                .inviteCode(user.getInviteCode())
                .build();
    }
}
