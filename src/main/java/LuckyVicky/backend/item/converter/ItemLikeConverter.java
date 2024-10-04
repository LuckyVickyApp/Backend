package LuckyVicky.backend.item.converter;

import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.domain.ItemLike;
import LuckyVicky.backend.user.domain.User;

public class ItemLikeConverter {
    public static ItemLike itemLike(User user, Item item) {
        return ItemLike.builder()
                .item(item)
                .user(user)
                .build();
    }
}
