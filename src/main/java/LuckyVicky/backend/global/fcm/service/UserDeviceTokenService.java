package LuckyVicky.backend.global.fcm.service;

import static LuckyVicky.backend.global.util.Constant.DEVICE_REGISTRATION_LIMIT;

import LuckyVicky.backend.global.fcm.domain.UserDeviceToken;
import LuckyVicky.backend.global.fcm.repository.UserDeviceTokenRepository;
import LuckyVicky.backend.user.converter.UserConverter;
import LuckyVicky.backend.user.domain.User;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceTokenService {

    private final UserDeviceTokenRepository userDeviceTokenRepository;

    @Transactional
    public void registerDeviceIfPossible(User user, String deviceToken) {
        if (!isDeviceTokenRegistered(user, deviceToken)) {
            if (!canRegisterMoreDevices(user)) {
                UserDeviceToken oldestDeviceToken = userDeviceTokenRepository.findTopByUserOrderByCreatedAtAsc(user);
                userDeviceTokenRepository.delete(oldestDeviceToken);
            }
            UserDeviceToken userDeviceToken = UserConverter.saveDeviceToken(user, deviceToken);
            userDeviceTokenRepository.save(userDeviceToken);
        }
    }

    private boolean isDeviceTokenRegistered(User user, String deviceToken) {
        List<UserDeviceToken> userDeviceTokenList = user.getDeviceTokenList();
        for (UserDeviceToken userDeviceToken : userDeviceTokenList) {
            if (Objects.equals(userDeviceToken.getDeviceToken(), deviceToken)) {
                return true;
            }
        }
        return false;
    }

    private boolean canRegisterMoreDevices(User user) {
        return user.getDeviceTokenList().size() < DEVICE_REGISTRATION_LIMIT;
    }
}
