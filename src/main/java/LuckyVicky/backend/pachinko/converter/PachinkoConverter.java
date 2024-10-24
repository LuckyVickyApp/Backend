package LuckyVicky.backend.pachinko.converter;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.pachinko.dto.PachinkoResponseDto.PachinkoRewardResDto;
import LuckyVicky.backend.user.domain.UserJewel;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PachinkoConverter {

    public static PachinkoRewardResDto pachinkoRewardResDto(List<Long> userJewelList){

        return PachinkoRewardResDto.builder()
                .jewelS(userJewelList.get(0))
                .jewelA(userJewelList.get(1))
                .jewelB(userJewelList.get(2))
                .build();

    }


}
