package LuckyVicky.backend.user.service;

import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.entity.Uuid;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.global.repository.UuidRepository;
import LuckyVicky.backend.global.s3.AmazonS3Manager;
import LuckyVicky.backend.user.converter.UserConverter;
import LuckyVicky.backend.user.domain.RefreshToken;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.dto.JwtDto;
import LuckyVicky.backend.user.dto.UserRequestDto;
import LuckyVicky.backend.user.dto.UserResponseDto;
import LuckyVicky.backend.user.jwt.JwtTokenUtils;
import LuckyVicky.backend.user.repository.RefreshTokenRepository;
import LuckyVicky.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JpaUserDetailsManager manager;
    private final JwtTokenUtils jwtTokenUtils;
    private final AmazonS3Manager amazonS3Manager;
    private final UuidRepository uuidRepository;

    public User findByUserName(String userName){
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND_BY_USERNAME));
    }

    public Boolean checkMemberByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User createUser(UserRequestDto.UserReqDto userReqDto) {
        Uuid uuid = Uuid.generateUuid();
        String nick = "user" + uuid.getUuid();
        uuidRepository.save(uuid);
        User newUser = userRepository.save(UserConverter.saveUser(userReqDto, nick));

        // 새로운 사용자 정보를 반환하기 전에 저장된 UserDetails를 다시 로드하여 동기화 시도
        manager.loadUserByUsername(userReqDto.getUsername());

        return newUser;
    }

    @Transactional
    public JwtDto jwtMakeSave(String username){

        // JWT 생성 - access & refresh
        UserDetails details
                = manager.loadUserByUsername(username);

        JwtDto jwt = jwtTokenUtils.generateToken(details); //2. access, refresh token 생성 및 발급
        log.info("accessToken: {}", jwt.getAccessToken());
        log.info("refreshToken: {} ", jwt.getRefreshToken());


        // DB에 저장된 해당 사용자의 리프레시 토큰을 업데이트
        Optional<RefreshToken> existingToken = refreshTokenRepository.findById(username);
        if (existingToken.isPresent()) {
            refreshTokenRepository.deleteById(username);
        }

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .username(username)
                        .refreshToken(jwt.getRefreshToken())
                        .build()
        );

        // JSON 형태로 응답
        return jwt;
    }

}
