package LuckyVicky.backend.pachinko.service;

import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.pachinko.domain.UserPachinko;
import LuckyVicky.backend.pachinko.repository.PachinkoRepository;
import LuckyVicky.backend.pachinko.repository.UserPachinkoRepository;
import LuckyVicky.backend.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 빠칭코 비즈니스 로직
@Slf4j
@Service
@RequiredArgsConstructor
public class PachinkoService {

    private final PachinkoRepository pachinkoRepository;
    private final UserPachinkoRepository userpachinkoRepository;

    @Getter
    private Set<Integer> selectedSquares = new HashSet<>();

    @Getter
    private Long currentRound = 1L;  // 현재 몇 판째인지 추적하는 변수

    // 게임 판을 초기화하거나, 새로운 판을 시작할 때 호출
    @Transactional
    public void startNewRound() {
        currentRound++;

        // pachinko 판 초기화 로직

    }

    @Transactional
    public boolean selectSquare(User user, long currentRound, int squareNumber) {
        // 6*6 안의 칸인지 확인
        if (squareNumber < 1 || squareNumber > 36) throw new GeneralException(ErrorCode.PACHINKO_OUT_OF_BOUND);

        // 이미 선택된 칸인지 확인
        if (selectedSquares.contains(squareNumber)) return false;

        // 이미 한번 이상 선택했었다면
        if (userpachinkoRepository.findByUserAndRound(user, currentRound).isPresent()){
            UserPachinko p = userpachinkoRepository.findByUserAndRound(user, currentRound)
                    .orElseThrow( () -> new GeneralException(ErrorCode.USER_PACHINKO_NOT_FOUND));

            if(p.getSquare2() == 0) p.setSquares(p.getSquare1(), squareNumber, 0);
            else if(p.getSquare3() == 0) p.setSquares(p.getSquare1(), p.getSquare2(), squareNumber);
            else throw new GeneralException(ErrorCode.PACHINKO_NO_MORE_CHANCE);
        }
        else{ // 처음 선택한거라면
            userpachinkoRepository.save(UserPachinko.builder()
                    .round(currentRound).user(user).square1(squareNumber).square2(0).square3(0).build());
        }

        // 선택되지 않은 칸이면 추가
        selectedSquares.add(squareNumber);
        if (selectedSquares.size() == 36){ // selectedSquares.containsAll(IntStream.rangeClosed(1, 36).boxed().collect(Collectors.toSet())
            // 보상 주기

            // 새로운 라운드 시작하기
            startNewRound();
        }

        return true;
    }

    public void updateSelectedSquaresSet(){
        // 라운드 업데이트
        currentRound = userpachinkoRepository.findCurrentRound();

        // set 업데이트
        List<UserPachinko> userPachinkoList = userpachinkoRepository.findByRound(currentRound);
        for (UserPachinko userPachinko : userPachinkoList) {
            selectedSquares.add(userPachinko.getSquare1());
            selectedSquares.add(userPachinko.getSquare2());
            selectedSquares.add(userPachinko.getSquare3());
        }
        selectedSquares.remove(0);
    }


}
