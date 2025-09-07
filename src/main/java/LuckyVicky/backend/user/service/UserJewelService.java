package LuckyVicky.backend.user.service;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserJewelService {

    public final UserJewelRepository userJewelRepository;
    private static final JewelType PACHINKO_NEED_JEWEL_TYPE = JewelType.B;
    private static final int PACHINKO_NEED_JEWEL_COUNT = 1;

    @Transactional
    public void deductUserJewel(User user) {
        // DB에서 락 걸고 사용자 보석 정보 조회
        UserJewel userJewel = userJewelRepository.findByUserAndJewelType(user, PACHINKO_NEED_JEWEL_TYPE)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_JEWEL_NOT_FOUND));
        // 선택을 위한 코색 개수가 있는지
        if (userJewel.getCount() < PACHINKO_NEED_JEWEL_COUNT) {
            throw new GeneralException(ErrorCode.PACHINKO_NO_MORE_JEWEL);
        }
        // 보석 수량 차감
        userJewel.decreaseCount(PACHINKO_NEED_JEWEL_COUNT);
        userJewelRepository.save(userJewel);
    }
}
