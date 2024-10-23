package LuckyVicky.backend.roulette.converter;

import LuckyVicky.backend.roulette.dto.RouletteResultDto;
import org.springframework.stereotype.Component;

@Component
public class RouletteConverter {

    // 룰렛 결과를 DTO로 변환
    public RouletteResultDto convertToDto(String message, int jewelCount) {
        return new RouletteResultDto(message, jewelCount);
    }
}
