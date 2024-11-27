package LuckyVicky.backend.enhance.service;

import LuckyVicky.backend.enhance.domain.EnhanceRate;
import LuckyVicky.backend.enhance.repository.EnhanceItemRepository;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.enhance.domain.EnhanceResult;
import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.enhance.repository.EnhanceRateRepository;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnhanceService {

    private final EnhanceRateRepository enhanceRateRepository;
    private final EnhanceItemRepository enhanceItemRepository;
    private final UserJewelRepository userJewelRepository;


    // 상품 강화 후, 결과 반환
    @Transactional
    public EnhanceResult getItemEnhanceResult(EnhanceItem enhanceItem, String jewelTypeStr) {

        // 현재 강화 레벨
        Integer currentEnhanceLevel = enhanceItem.getEnhanceLevel();

        // 사용할 보석 종류
        JewelType jewelType = getJewelType(jewelTypeStr);

        // 사용자 보유 보석 개수 체크 & 사용
        checkAndConsumeUserJewel(enhanceItem.getUser(), jewelType);

        // 강화 확률
        EnhanceRate enhanceRate = enhanceRateRepository.findByEnhanceLevelAndJewelType(currentEnhanceLevel, jewelType)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ENHANCE_RATE_NOT_FOUND));

        BigDecimal upRate = enhanceRate.getUpRate();           // 성공 확률
        BigDecimal downRate = enhanceRate.getDownRate();       // 실패 확률
        BigDecimal keepRate = enhanceRate.getKeepRate();       // 유지 확률
        BigDecimal destroyRate = enhanceRate.getDestroyRate(); // 파괴 확률

        // 난수 생성 (0.0 ~ 1.0)
        BigDecimal randomValue = BigDecimal.valueOf(Math.random());

        // 로그
        System.out.println("난수: " + randomValue);
        System.out.println("성공 확률: " + upRate);
        System.out.println("실패 확률: " + downRate);
        System.out.println("유지 확률: " + keepRate);
        System.out.println("파괴 확률: " + destroyRate);

        // 강화 결과 반환
        return processEnhanceResult(enhanceItem, randomValue, upRate, downRate, keepRate);
    }


    // 강화 결과 처리
    private EnhanceResult processEnhanceResult(EnhanceItem enhanceItem, BigDecimal randomValue,
                                               BigDecimal upRate, BigDecimal downRate, BigDecimal keepRate) {

        // 강화 성공
        if (randomValue.compareTo(upRate) < 0) {
            return updateEnhanceItem(enhanceItem, EnhanceResult.UP);
        }
        // 강화 실패
        else if (randomValue.compareTo(upRate.add(downRate)) < 0) {
            return updateEnhanceItem(enhanceItem, EnhanceResult.DOWN);
        }
        // 강화 유지
        else if (randomValue.compareTo(upRate.add(downRate).add(keepRate)) < 0) {
            return updateEnhanceItem(enhanceItem, EnhanceResult.KEEP);
        }
        // 강화 파괴
        else {
            return updateEnhanceItem(enhanceItem, EnhanceResult.DESTROY);
        }

    }

    // 결과에 따른 강화 아이템 상태 및 랭킹 갱신
    private EnhanceResult updateEnhanceItem(EnhanceItem enhanceItem, EnhanceResult result) {
        // 레벨 & 시도 횟수 갱신
        switch (result) {
            case UP:
                enhanceItem.upEnhanceItem();
                break;
            case DOWN:
                enhanceItem.downEnhanceItem();
                break;
            case DESTROY:
                enhanceItem.destroyItem();
                break;
            case KEEP:
                enhanceItem.keepEnhanceItem();
                break;
        }
        enhanceItemRepository.save(enhanceItem);

        // 랭킹 갱신
        updateUserRanking(enhanceItem);

        return result;

    }

    // String -> JewelType 변환
    private JewelType getJewelType(String jewelTypeStr) {
        try {
            return JewelType.valueOf(jewelTypeStr);
        } catch (IllegalArgumentException e) {
            throw GeneralException.of(ErrorCode.ENHANCE_JEWEL_NOT_FOUND);
        }
    }

    // 해당 상품 랭킹 갱신
    private void updateUserRanking(EnhanceItem enhanceItem) {

        // 1. 해당 상품의 EnhanceItem 리스트 정렬
        List<EnhanceItem> enhanceItems
                = enhanceItemRepository.findEnhanceItemsByItemOrderByEnhanceLevelAndReachedTime(enhanceItem.getItem());

        // 2. 랭킹 부여
        for (int i = 0; i < enhanceItems.size(); i++) {
            EnhanceItem item = enhanceItems.get(i);
            item.updateRanking(i + 1);
            item.updateIsGet(item.getItem().getQuantity());
            enhanceItemRepository.save(item);
        }
    }

    // 사용자 보유 보석 개수 체크 및 사용
    private void checkAndConsumeUserJewel(User user, JewelType jewelType) {
        UserJewel userJewel = user.getUserJewelList().stream()
                .filter(jewel -> jewel.getJewelType() == jewelType)
                .findFirst()
                .orElseThrow(() -> GeneralException.of(ErrorCode.ENHANCE_JEWEL_NOT_FOUND));

        if (userJewel.getCount() > 0) {
            userJewel.decreaseCount(1);
            userJewelRepository.save(userJewel);
        } else {
            throw GeneralException.of(ErrorCode.ENHANCE_JEWEL_NOT_ENOUGH);
        }
    }
}
