package LuckyVicky.backend.roulette.service;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import LuckyVicky.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RouletteService {

    private final UserJewelRepository userJewelRepository;
    private final UserRepository userRepository;

    @Transactional
    public String[] spinRoulette(User user) {
        BigDecimal randomValue = BigDecimal.valueOf(Math.random());

        // 룰렛 확률 설정
        BigDecimal noRewardProbability = BigDecimal.valueOf(0.30);
        BigDecimal bGrade1Probability = BigDecimal.valueOf(0.40);
        BigDecimal bGrade2Probability = BigDecimal.valueOf(0.10);
        BigDecimal bGrade3Probability = BigDecimal.valueOf(0.10);
        BigDecimal aGrade1Probability = BigDecimal.valueOf(0.09);

        String message;
        int jewelCount;

        // 룰렛 결과를 판단하는 로직
        if (randomValue.compareTo(noRewardProbability) < 0) {
            message = "꽝";
            jewelCount = 0;
        } else if (randomValue.compareTo(noRewardProbability.add(bGrade1Probability)) < 0) {
            addJewel(user, "B", 1);
            message = "B급 보석 1개";
            jewelCount = 1;
        } else if (randomValue.compareTo(noRewardProbability.add(bGrade1Probability).add(bGrade2Probability)) < 0) {
            addJewel(user, "B", 2);
            message = "B급 보석 2개";
            jewelCount = 2;
        } else if (randomValue.compareTo(noRewardProbability.add(bGrade1Probability).add(bGrade2Probability).add(bGrade3Probability)) < 0) {
            addJewel(user, "B", 3);
            message = "B급 보석 3개";
            jewelCount = 3;
        } else if (randomValue.compareTo(noRewardProbability.add(bGrade1Probability).add(bGrade2Probability).add(bGrade3Probability).add(aGrade1Probability)) < 0) {
            addJewel(user, "A", 1);
            message = "A급 보석 1개";
            jewelCount = 1;
        } else {
            addJewel(user, "A", 2);
            message = "A급 보석 2개";
            jewelCount = 2;
        }

        return new String[] { message, String.valueOf(jewelCount) };
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
