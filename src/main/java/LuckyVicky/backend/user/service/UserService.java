package LuckyVicky.backend.user.service;

import static LuckyVicky.backend.global.util.Constant.PHONE_NUMBER_PATTERN;
import static org.apache.logging.log4j.util.Strings.isEmpty;

import LuckyVicky.backend.aes.service.AesEncryptService;
import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.entity.Uuid;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.global.fcm.domain.UserDeviceToken;
import LuckyVicky.backend.global.fcm.repository.UserDeviceTokenRepository;
import LuckyVicky.backend.global.repository.UuidRepository;
import LuckyVicky.backend.global.s3.AmazonS3Manager;
import LuckyVicky.backend.user.converter.UserConverter;
import LuckyVicky.backend.user.domain.RefreshToken;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.dto.JwtDto;
import LuckyVicky.backend.user.dto.UserRequestDto;
import LuckyVicky.backend.user.dto.UserRequestDto.UserReqDto;
import LuckyVicky.backend.user.jwt.JwtTokenUtils;
import LuckyVicky.backend.user.repository.RefreshTokenRepository;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import LuckyVicky.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDeviceTokenRepository userDeviceTokenRepository;
    private final JpaUserDetailsManager manager;
    private final JwtTokenUtils jwtTokenUtils;
    private final AmazonS3Manager amazonS3Manager;
    private final UuidRepository uuidRepository;
    private final UserJewelRepository userJewelRepository;
    private final AesEncryptService aesEncryptService;

    @Transactional
    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND_BY_USERNAME));
    }

    @Transactional
    public Boolean checkMemberByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User createUser(UserReqDto userReqDto) {
        Uuid uuid = Uuid.generateUuid();
        String nick = "user" + uuid.getUuid();
        uuidRepository.save(uuid);
        User newUser = userRepository.save(UserConverter.saveUser(userReqDto, nick));

        // 새로운 사용자 정보를 반환하기 전에 저장된 UserDetails를 다시 로드하여 동기화 시도
        manager.loadUserByUsername(userReqDto.getUsername());
        return newUser;
    }

    @Transactional
    public void makeUserJewel(User user) {
        userJewelRepository.save(UserJewel.builder().user(user).jewelType(JewelType.S).count(0).build());
        userJewelRepository.save(UserJewel.builder().user(user).jewelType(JewelType.A).count(5).build());
        userJewelRepository.save(UserJewel.builder().user(user).jewelType(JewelType.B).count(10).build());
    }

    @Transactional
    public void registerDeviceToken(User user, String deviceToken) {
        UserDeviceToken userDeviceToken = userDeviceTokenRepository.save(
                UserConverter.saveDeviceToken(user, deviceToken));
        userDeviceTokenRepository.save(userDeviceToken);
    }

    @Transactional
    public JwtDto jwtMakeSave(String username) {
        UserDetails details
                = manager.loadUserByUsername(username);

        JwtDto jwt = jwtTokenUtils.generateToken(details);
        log.info("accessToken: {}", jwt.getAccessToken());
        log.info("refreshToken: {} ", jwt.getRefreshToken());

        // 리프레시 토큰 업데이트
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

        return jwt;
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").split(" ")[1];

        String username = jwtTokenUtils.parseClaims(accessToken).getSubject();
        log.info("access token에서 추출한 username : {}", username);
        if (refreshTokenRepository.existsByUsername(username)) {
            refreshTokenRepository.deleteByUsername(username);
            log.info("DB에서 리프레시 토큰 삭제 완료");
        } else {
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }
    }

    @Transactional
    public JwtDto reissue(HttpServletRequest request) {
        // 1. Request에서 Refresh Token 추출
        String refreshTokenValue = request.getHeader("Authorization").split(" ")[1];

        // 2. DB에서 해당 Refresh Token을 찾음
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
                .orElseThrow(() -> new GeneralException(ErrorCode.WRONG_REFRESH_TOKEN));
        log.info("찾은 refresh token : {}", refreshToken);

        // 3. Refresh Token을 발급한 사용자 정보 로드
        UserDetails userDetails = manager.loadUserByUsername(refreshToken.getUsername());
        log.info("refresh token에서 추출한 username : {}", refreshToken.getUsername());

        // 4. 새로운 Access Token 및 Refresh Token 생성, 저장
        JwtDto jwt = jwtTokenUtils.generateToken(userDetails);
        log.info("reissue: refresh token 재발급 완료");
        refreshToken.updateRefreshToken(jwt.getRefreshToken());

        log.info("accessToken: {}", jwt.getAccessToken());
        log.info("refreshToken: {} ", jwt.getRefreshToken());

        // 5. DB에 새로운 리프레시 토큰이 정상적으로 저장되었는지 확인
        if (!refreshTokenRepository.existsByUsername(refreshToken.getUsername())) {
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }

        return jwt;
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        if (refreshTokenRepository.existsByUsername(username)) {
            refreshTokenRepository.deleteByUsername(username);
            log.info("DB에서 리프레시 토큰 삭제 완료");
        }
        userRepository.delete(user);
        log.info("{} 회원 탈퇴 완료", username);
    }

    @Transactional
    public void saveNickname(UserRequestDto.UserNicknameReqDto nicknameReqDto, User user) {
        String nickname = nicknameReqDto.getNickname();

        if (userRepository.existsByNickname(nickname)) {
            throw GeneralException.of(ErrorCode.ALREADY_USED_NICKNAME);
        }
        user.updateNickname(nickname);
    }

    @Transactional
    public void updateDeliveryInformation(User user, UserRequestDto.UserAddressReqDto userAddressReqDto)
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        if (userAddressReqDto.getStreetAddress() == null) {
            throw GeneralException.of(ErrorCode.USER_ADDRESS_NULL);
        }

        String phoneNumber = userAddressReqDto.getPhoneNumber();
        if (!phoneNumber.matches(PHONE_NUMBER_PATTERN)) {
            throw GeneralException.of(ErrorCode.INVALID_PHONE_NUMBER_FORMAT);
        }

        String encryptedPhoneNumber = aesEncryptService.encryptPhoneNumberWithAES(phoneNumber);

        user.updateDeliveryInformation(
                userAddressReqDto.getRecipientName(),
                encryptedPhoneNumber,
                userAddressReqDto.getStreetAddress(),
                userAddressReqDto.getDetailedAddress()
        );

        userRepository.save(user);
    }

    @Transactional
    public void updateProfileImage(MultipartFile file, User user) throws IOException {
        String uploadFileUrl = null;
        String dirName = "profile/";

        if (file != null) {
            String contentType = file.getContentType();
            if (ObjectUtils.isEmpty(contentType)) {
                throw GeneralException.of(ErrorCode.ITEM_IMAGE_UPLOAD_FAILED);
            }

            MediaType mediaType = amazonS3Manager.contentType(Objects.requireNonNull(file.getOriginalFilename()));
            if (mediaType == null || !(mediaType.equals(MediaType.IMAGE_PNG) || mediaType.equals(
                    MediaType.IMAGE_JPEG))) {
                throw GeneralException.of(ErrorCode.ITEM_IMAGE_UPLOAD_FAILED);
            }

            if (!isEmpty(user.getProfileImage())) {
                String previousFilePath = user.getProfileImage();
                amazonS3Manager.delete(previousFilePath);
            }

            java.io.File uploadFile = amazonS3Manager.convert(file)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

            String fileName = dirName + AmazonS3Manager.generateFileName(file);
            uploadFileUrl = amazonS3Manager.putS3(uploadFile, fileName);

            user.updateProfileImage(uploadFileUrl);
            userRepository.save(user);
        }
    }
}
