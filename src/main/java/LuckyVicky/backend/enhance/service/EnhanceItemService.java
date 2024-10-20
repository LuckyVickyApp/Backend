package LuckyVicky.backend.enhance.service;

import LuckyVicky.backend.enhance.converter.EnhanceConverter;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.enhance.repository.EnhanceItemRepository;
import LuckyVicky.backend.user.domain.User;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnhanceItemService {

    private final EnhanceItemRepository enhanceItemRepository;

    @Transactional
    public EnhanceItem findByUserAndItem(User user, Item item) {
        return enhanceItemRepository.findByUserAndItem(user, item)
                // 없으면 새로운 Entity 생성
                .orElseGet(() -> {
                    EnhanceItem newEnhanceItem = EnhanceConverter.createEnhanceItem(user, item);
                    return enhanceItemRepository.save(newEnhanceItem);
                });
    }

}
