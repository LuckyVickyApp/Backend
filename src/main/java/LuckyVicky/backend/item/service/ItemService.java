package LuckyVicky.backend.item.service;

import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.item.converter.ItemConverter;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.dto.ItemRequestDto;
import LuckyVicky.backend.item.dto.ItemResponseDto;
import LuckyVicky.backend.item.repository.ItemRepository;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.global.s3.AmazonS3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemConverter itemConverter;
    private final AmazonS3Manager amazonS3Manager;

    @Transactional
    public ItemResponseDto createItem(ItemRequestDto requestDto) throws IOException {
        String uploadFileUrl = null;

        if (requestDto.getImageFile() != null) {
            uploadFileUrl = uploadImageToS3(requestDto.getImageFile());
        }

        Item item = itemConverter.toEntity(requestDto, uploadFileUrl);
        itemRepository.save(item);

        return itemConverter.toDto(item);
    }

    public List<ItemResponseDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(itemConverter::toDto)
                .collect(Collectors.toList());
    }

    public ItemResponseDto getItemByName(String name) {
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
}
