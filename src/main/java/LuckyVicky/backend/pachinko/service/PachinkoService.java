package LuckyVicky.backend.pachinko.service;

import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.pachinko.domain.Pachinko;
import LuckyVicky.backend.pachinko.domain.PachinkoReward;
import LuckyVicky.backend.pachinko.domain.UserPachinko;
import LuckyVicky.backend.pachinko.repository.PachinkoRepository;
import LuckyVicky.backend.pachinko.repository.PachinkoRewardRepository;
import LuckyVicky.backend.pachinko.repository.UserPachinkoRepository;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PachinkoService {

    private final PachinkoRepository pachinkoRepository;
    private final UserPachinkoRepository userpachinkoRepository;
    private final PachinkoRewardRepository pachinkoRewardRepository;
    private final UserJewelRepository userJewelRepository;

    @Getter
    private Set<Integer> selectedSquares = new HashSet<>();

    @Getter
    private Long currentRound = 1L;

    public void startFirstRound(){
        assignRewardsToSquares(currentRound);
    }

    @Transactional
    public void startNewRound() {
        System.out.println("라운드 갱신");
        currentRound++;

        System.out.println("파창코 set 초기화");
        selectedSquares.clear();

        System.out.println("파칭코 칸에 보상 부여");
        assignRewardsToSquares(currentRound);
    }

    @Transactional
    public boolean noMoreJewel(User user){
        UserJewel userJewelB = userJewelRepository.findByUserAndJewelType(user, JewelType.B)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_JEWEL_SERVER_ERROR));

        return userJewelB.getCount() <= 0;
    }

    @Transactional
    public boolean canSelectMore(User user, Long round) {
        // Optional로 조회하여 값이 없으면 true 반환, 있으면 조건에 맞게 처리
        return userpachinkoRepository.findByUserAndRound(user, round)
                .map(userPachinko -> userPachinko.getSquare3() == 0)  // 존재할 때 조건에 맞게 처리
                .orElse(true);  // 존재하지 않으면 true 반환
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
            else return false;
        }
        else{ // 처음 선택한거라면
            userpachinkoRepository.save(UserPachinko.builder()
                    .round(currentRound).user(user).square1(squareNumber).square2(0).square3(0).build());
        }
        selectedSquares.add(squareNumber);
        return true;
    }

    public boolean isGameOver() {
        System.out.println("모든 칸 선택 되었나 확인중");
        return selectedSquares.size() == 36;
    }

    public void giveRewards(){
        System.out.println("보상 전달 시작");
        List<UserPachinko> userPachinkoList = userpachinkoRepository.findByRound(currentRound);

        for (UserPachinko userPachinko : userPachinkoList) {
            User user = userPachinko.getUser();

            List<Integer> squares = new ArrayList<>();
            squares.add(userPachinko.getSquare1());
            squares.add(userPachinko.getSquare2());
            squares.add(userPachinko.getSquare3());

            for(int i = 0; i < 3; i++){
                if(squares.get(i) > 0){
                    int sq = squares.get(i);
                    // 해당 칸에 대한 보상 알아내기
                    Pachinko pa = pachinkoRepository.findByRoundAndSquare(currentRound, sq)
                            .orElseThrow( () -> new GeneralException(ErrorCode.BAD_REQUEST));
                    if(pa.getJewelType() != JewelType.F){
                        // 보상에 맞는 보석 종류의 보석함 엔티티 알아내기
                        UserJewel uj = userJewelRepository.findByUserAndJewelType(user, pa.getJewelType())
                                .orElseThrow( () -> new GeneralException(ErrorCode.BAD_REQUEST));
                        // 보석 개수 늘리기
                        uj.setCount(pa.getJewelNum());
                        userJewelRepository.save(uj);
                    }
                }
            }
            System.out.println("보상 전달 완료");
        }
        System.out.println("새로운 라운드 시작");
        startNewRound();
    }

    public void assignRewardsToSquares(Long currentRound){
        // 보상 항목들을 리스트에 추가
        List<String> rewards = new ArrayList<>();

        PachinkoReward s1 = pachinkoRewardRepository.findByJewelTypeAndJewelNum(JewelType.S, 1)
                .orElseThrow(() -> new GeneralException(ErrorCode.BAD_REQUEST));
        PachinkoReward a1 = pachinkoRewardRepository.findByJewelTypeAndJewelNum(JewelType.A, 1)
                .orElseThrow(() -> new GeneralException(ErrorCode.BAD_REQUEST));
        PachinkoReward b2 = pachinkoRewardRepository.findByJewelTypeAndJewelNum(JewelType.B, 2)
                .orElseThrow(() -> new GeneralException(ErrorCode.BAD_REQUEST));
        PachinkoReward b1 = pachinkoRewardRepository.findByJewelTypeAndJewelNum(JewelType.B, 1)
                .orElseThrow(() -> new GeneralException(ErrorCode.BAD_REQUEST));

        for(int i = 0; i < s1.getSquareCount(); i++){
            rewards.add("S1");
        }
        for(int i = 0; i < a1.getSquareCount(); i++){
            rewards.add("A1");
        }
        for(int i = 0; i < b2.getSquareCount(); i++){
            rewards.add("B2");
        }
        for(int i = 0; i < b1.getSquareCount(); i++){
            rewards.add("B1");
        }
        for(int i = 0; i < 36 - (s1.getSquareCount() + a1.getSquareCount() + b2.getSquareCount() + b1.getSquareCount()); i++){
            rewards.add("F");
        }

        SecureRandom secureRandom = new SecureRandom();
        Collections.shuffle(rewards, secureRandom);

        // db에 넣기
        for(int i = 1; i <= 36; i++){
            JewelType jewelType;
            int jewelNum;
            if(Objects.equals(rewards.get(i - 1), "S1")) {
                jewelType = JewelType.S; jewelNum = 1;
            }
            else if(Objects.equals(rewards.get(i - 1), "A1")) {
                jewelType = JewelType.A; jewelNum = 1;
            }
            else if(Objects.equals(rewards.get(i - 1), "B2")) {
                jewelType = JewelType.B; jewelNum = 2;
            }
            else if(Objects.equals(rewards.get(i - 1), "B1")) {
                jewelType = JewelType.B; jewelNum = 1;
            }
            else {
                jewelType = JewelType.F; jewelNum = 0;
            }

            pachinkoRepository.save(Pachinko.builder()
                    .round(currentRound).square(i).jewelType(jewelType).jewelNum(jewelNum).build());
            System.out.println("각각의 칸에 보상 설정 완료");
        }
    }

    // 서버 내린다음 다시 올릴때 이전 게임 로딩
    public void updateSelectedSquaresSet(){

        if (selectedSquares.size() == 36){
            currentRound = userpachinkoRepository.findCurrentRound() + 1;
        }
        else{
            currentRound = userpachinkoRepository.findCurrentRound();

            List<UserPachinko> userPachinkoList = userpachinkoRepository.findByRound(currentRound);
            for (UserPachinko userPachinko : userPachinkoList) {
                selectedSquares.add(userPachinko.getSquare1());
                selectedSquares.add(userPachinko.getSquare2());
                selectedSquares.add(userPachinko.getSquare3());
            }
            selectedSquares.remove(0);
        }
    }

}
