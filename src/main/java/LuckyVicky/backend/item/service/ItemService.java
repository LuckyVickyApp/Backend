package LuckyVicky.backend.item.service;

import LuckyVicky.backend.item.converter.ItemConverter;
import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.item.dto.ItemRequestDto;
import LuckyVicky.backend.item.dto.ItemResponseDto;
import LuckyVicky.backend.item.repository.ItemRepository;
import LuckyVicky.backend.global.s3.AmazonS3Manager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemConverter itemConverter;
    private final AmazonS3Manager amazonS3Manager;

    public ItemService(ItemRepository itemRepository, ItemConverter itemConverter, AmazonS3Manager amazonS3Manager) {
        this.itemRepository = itemRepository;
        this.itemConverter = itemConverter;
        this.amazonS3Manager = amazonS3Manager;
    }

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

    private String uploadImageToS3(MultipartFile imageFile) throws IOException {
        return amazonS3Manager.putS3(amazonS3Manager.convert(imageFile)
                        .orElseThrow(() -> new IllegalArgumentException("파일 변환 실패")),
                AmazonS3Manager.generateFileName(imageFile));
    }
}
