package LuckyVicky.backend.roulette.service;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.roulette.dto.RouletteResponseDto;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import LuckyVicky.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouletteService {

    private final UserJewelRepository userJewelRepository;
    private final UserRepository userRepository;

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

        // F 보석인 경우에는 보상 저장을 건너뜀
        if (JewelType.valueOf(jewelType) == JewelType.F) {
            log.info("사용자 {}는 보상 없이 10분 타이머를 갱신합니다.", user.getUsername());
        } else {
            addJewel(user, jewelType, jewelCount);
        }

        // 10초호 다시 룰렛 사용 가능
        user.setRouletteAvailableTime(LocalDateTime.now().plusSeconds(30));

        userRepository.save(user);
    }

    private void addJewel(User user, String jewelType, int count) {
        UserJewel jewel = userJewelRepository.findFirstByUserAndJewelType(user, JewelType.valueOf(jewelType));
        if (jewel == null) {
            throw new GeneralException(ErrorCode.ENHANCE_JEWEL_NOT_FOUND);
        }
        jewel.increaseCount(count);
        userJewelRepository.save(jewel);
    }
}
