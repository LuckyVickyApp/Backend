package LuckyVicky.backend.roulette.service;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.global.fcm.converter.FcmConverter;
import LuckyVicky.backend.global.fcm.domain.UserDeviceToken;
import LuckyVicky.backend.global.fcm.dto.FcmRequestDto.FcmSimpleReqDto;
import LuckyVicky.backend.global.fcm.service.FcmService;
import LuckyVicky.backend.global.util.Constant;
import LuckyVicky.backend.roulette.dto.RouletteResponseDto;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import LuckyVicky.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouletteService {

    private final UserJewelRepository userJewelRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;
    private final TaskScheduler taskScheduler; // 동적 스케줄러

    @Transactional
    public RouletteResponseDto.RouletteAvailableDto checkRouletteAvailability(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime availableTime = user.getRouletteAvailableTime();

        if (availableTime != null && now.isBefore(availableTime)) {
            int remainingTime = (int) Duration.between(now, availableTime).getSeconds();
            return new RouletteResponseDto.RouletteAvailableDto(false, remainingTime);
        }

        return new RouletteResponseDto.RouletteAvailableDto(true, 0);
    }

    @Transactional
    public void saveRouletteResult(User user, String jewelType, int jewelCount) {
        log.info("사용자 {}에게 {} 보석 {}개 추가", user.getUsername(), jewelType, jewelCount);

        if (JewelType.valueOf(jewelType) != JewelType.F) {
            addJewel(user, jewelType, jewelCount);
        }

        user.setRouletteAvailableTime(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        scheduleRouletteNotification(user, 10);
    }

    private void addJewel(User user, String jewelType, int count) {
        UserJewel jewel = userJewelRepository.findFirstByUserAndJewelType(user, JewelType.valueOf(jewelType));
        if (jewel == null) {
            throw new GeneralException(ErrorCode.ENHANCE_JEWEL_NOT_FOUND);
        }
        jewel.increaseCount(count);
        userJewelRepository.save(jewel);
    }

    private void scheduleRouletteNotification(User user, int delayInMinutes) {
        taskScheduler.schedule(() -> {
                    try {
                        sendRouletteNotification(user);
                    } catch (IOException e) {
                        log.error("알림 전송 중 오류 발생: 사용자 {}, 메시지: {}", user.getUsername(), e.getMessage(), e);
                    }
                },
                LocalDateTime.now().plusMinutes(delayInMinutes).atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    private void sendRouletteNotification(User user) throws IOException {

        List<UserDeviceToken> userDeviceTokens = user.getDeviceTokenList();
        for (UserDeviceToken userDeviceToken : userDeviceTokens) {
            FcmSimpleReqDto requestDTO = FcmConverter.toFcmSimpleReqDto(userDeviceToken.getDeviceToken(),
                    Constant.FCM_ROULETTE_CAN_START_TITLE, Constant.FCM_ROULETTE_CAN_START_BODY);
            fcmService.sendMessageTo(requestDTO);
        }
    }

}
