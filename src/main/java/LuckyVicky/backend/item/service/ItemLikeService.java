package LuckyVicky.backend.item.service;

import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.item.converter.ItemLikeConverter;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.domain.ItemLike;
import LuckyVicky.backend.item.repository.ItemLikeRepository;
import LuckyVicky.backend.item.repository.ItemRepository;
import LuckyVicky.backend.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemLikeService {
    private final ItemRepository itemRepository;
    private final ItemLikeRepository itemLikeRepository;

    @Transactional
    public Integer createLikeAndRetrieveCount(Long itemId, User user) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ITEM_NOT_FOUND));

        ItemLike itemLike = itemLikeRepository.findByUserAndItem(user, item).orElse(null);

        // 이미 좋아요 했다면,
        if (itemLike != null) {
            throw new GeneralException(ErrorCode.ITEM_ALREADY_LIKE);
        }
        // 좋아요 하지 않았다면
        else {
            itemLikeRepository.save(ItemLikeConverter.itemLike(user, item));
            return item.increaseLikeCount();
        }
    }

    @Transactional
    public Integer deleteLikeAndRetrieveCount(Long itemId, User user) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ITEM_NOT_FOUND));

        ItemLike itemLike = itemLikeRepository.findByUserAndItem(user, item).orElse(null);

        // 이미 좋아요 했다면,
        if (itemLike != null) {
            itemLikeRepository.delete(itemLike);
            return item.decreaseLikeCount();
        }
        // 좋아요 하지 않았다면
        else {
            throw new GeneralException(ErrorCode.ITEM_LIKE_NOT_FOUND);
        }
    }
}
