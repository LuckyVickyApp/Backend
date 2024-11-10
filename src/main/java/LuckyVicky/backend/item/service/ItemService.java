package LuckyVicky.backend.item.service;

import static LuckyVicky.backend.global.util.Parser.parseString;

import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.global.s3.AmazonS3Manager;
import LuckyVicky.backend.item.converter.ItemConverter;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.dto.ItemRequestDto;
import LuckyVicky.backend.item.dto.ItemResponseDto.ItemDescriptionResListDto;
import LuckyVicky.backend.item.dto.ItemResponseDto.ItemDetailResDto;
import LuckyVicky.backend.item.repository.ItemRepository;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemConverter itemConverter;
    private final AmazonS3Manager amazonS3Manager;

    // 현재 강화할 수 있는 상품 리스트 반환
    @Transactional
    public List<Item> getEnhanceItemList() {
        LocalDateTime curTime = LocalDateTime.now();
        DayOfWeek dayOfWeek = curTime.getDayOfWeek();

        // 강화 기간(월요일 ~ 일요일 오후 9시까지)이 아닐 경우
        if (dayOfWeek == DayOfWeek.SUNDAY && curTime.toLocalTime().isAfter(LocalTime.of(21, 0))) {
            // 빈 리스트 반환
            return new ArrayList<>();
        }

        LocalDate curDate = curTime.toLocalDate();

        // 강화 가능 상품 리스트 반환
        return itemRepository.findItemsEligibleForEnhancement(curDate);
    }

    @Transactional
    public List<Item> getWeekItemList(LocalDate localDate) {
        return itemRepository.findItemsEligibleForEnhancement(localDate);
    }

    @Transactional
    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ITEM_NOT_FOUND));

    }

    @Transactional
    public ItemDetailResDto createItem(ItemRequestDto requestDto) throws IOException {
        String uploadFileUrl = null;

        if (requestDto.getImageFile() != null) {
            uploadFileUrl = uploadImageToS3(requestDto.getImageFile());
        }

        Item item = itemConverter.toEntity(requestDto, uploadFileUrl);
        itemRepository.save(item);

        return itemConverter.toDto(item);
    }

    public List<ItemDetailResDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(itemConverter::toDto)
                .collect(Collectors.toList());
    }

    public ItemDetailResDto getItemByName(String name) {
        Item item = itemRepository.findByName(name)
                .orElseThrow(() -> new GeneralException(ErrorCode.ITEM_NOT_FOUND));
        return itemConverter.toDto(item);
    }

    @Transactional
    public void deleteItemByName(String name) {
        // 해당 이름의 상품이 있는지 확인
        Item item = itemRepository.findByName(name)
                .orElseThrow(() -> new GeneralException(ErrorCode.ITEM_NOT_FOUND));

        // 상품 삭제
        itemRepository.delete(item);
    }

    private String uploadImageToS3(MultipartFile imageFile) throws IOException {
        return amazonS3Manager.putS3(amazonS3Manager.convert(imageFile)
                        .orElseThrow(() -> new IllegalArgumentException("파일 변환 실패")),
                AmazonS3Manager.generateFileName(imageFile));
    }

    public ItemDescriptionResListDto getItemDescription(Long id) {
        Item item = itemRepository.findItemById(id)
                .orElseThrow(() -> new GeneralException(ErrorCode.ITEM_NOT_FOUND));

        String descriptionKey = item.getDescriptionKey();
        String descriptionValue = item.getDescriptionValue();

        List<String> keys = parseString(descriptionKey);
        List<String> values = parseString(descriptionValue);

        return ItemConverter.itemDescriptionResListDto(item, keys, values);
    }
}
