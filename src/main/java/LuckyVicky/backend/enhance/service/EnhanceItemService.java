package LuckyVicky.backend.enhance.service;

import LuckyVicky.backend.enhance.converter.EnhanceConverter;
import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.ItemEnhanceResDto;
import LuckyVicky.backend.enhance.dto.EnhanceResponseDto.ItemForEnhanceResDto;
import LuckyVicky.backend.enhance.repository.EnhanceItemRepository;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.global.fcm.converter.FcmConverter;
import LuckyVicky.backend.global.fcm.domain.UserDeviceToken;
import LuckyVicky.backend.global.fcm.dto.FcmRequestDto.FcmSimpleReqDto;
import LuckyVicky.backend.global.fcm.service.FcmService;
import LuckyVicky.backend.global.util.Constant;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.service.ItemService;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.dto.UserJewelResponseDto.UserJewelResDto;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnhanceItemService {
    private static final int DEFAULT_ENHANCE_LEVEL = 1;
    private final EnhanceItemRepository enhanceItemRepository;
    private final ItemService itemService;
    private final FcmService fcmService;

    public EnhanceItem findByUserAndItem(User user, Item item) {
        return enhanceItemRepository.findByUserAndItem(user, item)
                .orElseThrow(() -> new GeneralException(ErrorCode.ENHANCE_ITEM_NOT_FOUND));
    }

    public Optional<EnhanceItem> findByUserAndItemInOptional(User user, Item item) {
        return enhanceItemRepository.findByUserAndItem(user, item);
    }

    public EnhanceItem findByUserAndItemOrCreateEnhanceItem(User user, Item item) {
        return enhanceItemRepository.findByUserAndItem(user, item)
                .orElseGet(() -> {
                    Integer lastRanking = enhanceItemRepository.countByItem(item) + 1;
                    EnhanceItem newEnhanceItem = EnhanceConverter.createEnhanceItem(user, item, lastRanking);
                    return enhanceItemRepository.save(newEnhanceItem);
                });
    }

    private Integer getEnhanceLevel(User user, Item item) {
        return enhanceItemRepository.findByUserAndItem(user, item)
                .map(EnhanceItem::getEnhanceLevel)
                .orElse(DEFAULT_ENHANCE_LEVEL);
    }

    public ItemForEnhanceResDto getItemForEnhanceResDto(User user, Item item) {
        Integer enhanceLevel = getEnhanceLevel(user, item);

        Boolean isItemLike = user.getItemLikeList().stream()
                .anyMatch(like -> like.getItem() == item);

        return EnhanceConverter.itemForEnhanceResDto(item, enhanceLevel, isItemLike);
    }

    @Transactional
    public ItemEnhanceResDto getItemEnhanceResDto(User user, List<Item> itemList) {
        List<ItemForEnhanceResDto> itemForEnhanceResDtoList
                = itemList.stream()
                .map(item -> getItemForEnhanceResDto(user, item))
                .toList();

        List<UserJewelResDto> userJewelResDtoList
                = user.getUserJewelList().stream()
                .map(EnhanceConverter::userJewelResDto)
                .toList();

        return EnhanceConverter.itemEnhanceResDto(itemForEnhanceResDtoList, userJewelResDtoList);
    }

    @Scheduled(cron = "0 0 21 ? * SUN", zone = "Asia/Seoul")
    public void currentWeekAward() throws IOException {
        executeCurrentWeekAward();
    }

    private void executeCurrentWeekAward() throws IOException {
        List<Item> currentWeekItemList = itemService.getWeekItemList(LocalDate.now());

        for (Item item : currentWeekItemList) {
            List<EnhanceItem> enhanceItemList = enhanceItemRepository.findEnhanceItemsByItemOrderByEnhanceLevelAndReachedTime(
                    item);

            for (EnhanceItem enhanceItem : enhanceItemList) {
                if (enhanceItem.getIsGet()) {
                    User user = enhanceItem.getUser();

                    List<UserDeviceToken> userDeviceTokenList = user.getDeviceTokenList();
                    for (UserDeviceToken userDeviceToken : userDeviceTokenList) {
                        String deviceToken = userDeviceToken.getDeviceToken();
                        FcmSimpleReqDto fcmSimpleReqDto = FcmConverter.toFcmSimpleReqDto(deviceToken,
                                Constant.FCM_CONGRATULATION, createFcmBody(user, item));
                        fcmService.sendMessageTo(fcmSimpleReqDto);
                    }
                }
            }
        }
    }

    private String createFcmBody(User user, Item item) {
        return "강화 보상으로" + user.getNickname() + "님이 " + item.getName() + "을(를) 획득하셨습니다.";
    }

}
