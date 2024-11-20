package LuckyVicky.backend.pachinko.converter;

import static LuckyVicky.backend.global.util.Constant.CONVERTER_INSTANTIATION_NOT_ALLOWED;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.pachinko.domain.Pachinko;
import LuckyVicky.backend.pachinko.domain.UserPachinko;
import LuckyVicky.backend.pachinko.dto.PachinkoResponseDto.PachinkoChosenResDto;
import LuckyVicky.backend.pachinko.dto.PachinkoResponseDto.PachinkoRewardResDto;
import LuckyVicky.backend.pachinko.dto.PachinkoResponseDto.PachinkoSquareRewardResDto;
import LuckyVicky.backend.pachinko.dto.PachinkoResponseDto.PachinkoUserRewardResDto;
import LuckyVicky.backend.user.domain.User;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PachinkoConverter {
    private PachinkoConverter() {
        throw new UnsupportedOperationException(CONVERTER_INSTANTIATION_NOT_ALLOWED);
    }

    public static PachinkoChosenResDto pachinkoChosenResDto(Set<Integer> meChosen, Set<Integer> chosenSquares) {
        chosenSquares.removeAll(meChosen);

        return PachinkoChosenResDto.builder()
                .meChosen(meChosen)
                .othersChosen(chosenSquares)
                .build();
    }

    public static PachinkoUserRewardResDto pachinkoUserRewardResDto(List<Long> userJewelList) {

        return PachinkoUserRewardResDto.builder()
                .jewelS(userJewelList.get(0))
                .jewelA(userJewelList.get(1))
                .jewelB(userJewelList.get(2))
                .build();

    }

    public static List<PachinkoSquareRewardResDto> pachinkoSquareRewardResDto(List<Pachinko> pachinkoList) {
        return pachinkoList.stream()
                .map(pachinko -> PachinkoSquareRewardResDto.builder()
                        .squareNum(pachinko.getSquare())
                        .jewelType(pachinko.getJewelType())
                        .JewelNum(pachinko.getJewelNum())
                        .build())
                .collect(Collectors.toList());
    }

    public static PachinkoRewardResDto pachinkoRewardResDto(List<Long> userJewelList, List<Pachinko> pachinkoList) {
        return PachinkoRewardResDto.builder()
                .pachinkoUserRewardResDto(pachinkoUserRewardResDto(userJewelList))
                .pachinkoSquareRewardResDtoList(pachinkoSquareRewardResDto(pachinkoList))
                .build();
    }

    public static Pachinko savePachinko(Long currentRound, Integer squareNum, JewelType jewelType, Integer jewelNum) {
        return Pachinko.builder()
                .round(currentRound)
                .square(squareNum)
                .jewelType(jewelType)
                .jewelNum(jewelNum)
                .build();
    }

    public static UserPachinko saveUserPachinko(Long currentRound, User user) {
        return UserPachinko.builder()
                .round(currentRound)
                .user(user)
                .square1(0)
                .square2(0)
                .square3(0)
                .build();
    }


}
