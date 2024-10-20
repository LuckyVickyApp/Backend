package LuckyVicky.backend.roulette.service;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.roulette.dto.RouletteResultDto;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import LuckyVicky.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j // 로그를 추가하기 위해 사용
public class RouletteService {

    private final UserJewelRepository userJewelRepository;
    private final UserRepository userRepository;

    @Transactional
    public RouletteResultDto spinRoulette(User user) {
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간과 rouletteAvailableTime을 비교
        log.info("현재 시간: {}", now);
        log.info("사용자의 룰렛 가능 시간: {}", user.getRouletteAvailableTime());

        if (user.getRouletteAvailableTime() != null && now.isBefore(user.getRouletteAvailableTime())) {
            log.warn("룰렛은 10분 내 재시도 불가.");
            throw new GeneralException(ErrorCode.ROULETTE_COOLDOWN); // 10분 제한 예외
        }

        BigDecimal randomValue = BigDecimal.valueOf(Math.random());
        String message;
        int jewelCount;

        // 룰렛 결과 판단
        if (randomValue.compareTo(BigDecimal.valueOf(0.30)) < 0) {
            message = "꽝";
            jewelCount = 0;
        } else if (randomValue.compareTo(BigDecimal.valueOf(0.70)) < 0) {
            message = "B급 보석 1개";
            addJewel(user, "B", 1);
            jewelCount = 1;
        } else if (randomValue.compareTo(BigDecimal.valueOf(0.80)) < 0) {
            message = "B급 보석 2개";
            addJewel(user, "B", 2);
            jewelCount = 2;
        } else if (randomValue.compareTo(BigDecimal.valueOf(0.90)) < 0) {
            message = "B급 보석 3개";
            addJewel(user, "B", 3);
            jewelCount = 3;
        } else if (randomValue.compareTo(BigDecimal.valueOf(0.99)) < 0) {
            message = "A급 보석 1개";
            addJewel(user, "A", 1);
            jewelCount = 1;
        } else {
            message = "A급 보석 2개";
            addJewel(user, "A", 2);
            jewelCount = 2;
        }

        // 10분 후에 룰렛을 다시 돌릴 수 있도록 설정
        user.setRouletteAvailableTime(now.plusMinutes(10));
        log.info("다음 룰렛 가능 시간: {}", user.getRouletteAvailableTime());

        userRepository.save(user);

        return new RouletteResultDto(message, jewelCount);
    }

    private void addJewel(User user, String jewelType, int count) {
        UserJewel jewel = userJewelRepository.findFirstByUserAndJewelType(user, JewelType.valueOf(jewelType));
        if (jewel == null) {
            throw new GeneralException(ErrorCode.ENHANCE_JEWEL_NOT_FOUND);
        }
        jewel.increaseCount(count);
        userJewelRepository.save(jewel);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }
}
