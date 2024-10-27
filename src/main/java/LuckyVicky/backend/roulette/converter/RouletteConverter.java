package LuckyVicky.backend.roulette.converter;

import LuckyVicky.backend.roulette.dto.RouletteDto;
import org.springframework.stereotype.Component;

@Component
public class RouletteConverter {

    public RouletteDto.RouletteResultDto convertToDto(String message, int jewelCount) {
        return new RouletteDto.RouletteResultDto(message, jewelCount);
    }
}
