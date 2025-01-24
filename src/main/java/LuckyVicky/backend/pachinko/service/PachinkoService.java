package LuckyVicky.backend.pachinko.service;

import LuckyVicky.backend.display_board.service.DisplayBoardService;
import LuckyVicky.backend.enhance.domain.JewelType;
import LuckyVicky.backend.global.api_payload.ErrorCode;
import LuckyVicky.backend.global.exception.GeneralException;
import LuckyVicky.backend.global.fcm.converter.FcmConverter;
import LuckyVicky.backend.global.fcm.domain.UserDeviceToken;
import LuckyVicky.backend.global.fcm.dto.FcmRequestDto.FcmSimpleReqDto;
import LuckyVicky.backend.global.fcm.service.FcmService;
import LuckyVicky.backend.global.util.Constant;
import LuckyVicky.backend.pachinko.converter.PachinkoConverter;
import LuckyVicky.backend.pachinko.domain.Pachinko;
import LuckyVicky.backend.pachinko.domain.PachinkoReward;
import LuckyVicky.backend.pachinko.domain.UserPachinko;
import LuckyVicky.backend.pachinko.repository.PachinkoRepository;
import LuckyVicky.backend.pachinko.repository.PachinkoRewardRepository;
import LuckyVicky.backend.pachinko.repository.UserPachinkoRepository;
import LuckyVicky.backend.user.domain.User;
import LuckyVicky.backend.user.domain.UserJewel;
import LuckyVicky.backend.user.repository.UserJewelRepository;
import LuckyVicky.backend.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PachinkoService {
    private static final int TOTAL_PACHINKO_SQUARE_COUNT = 36;
    private static final int MIN_PACHINKO_SQUARE_NUMBER = 1;
    private static final String REWARD_S1 = "S1";
    private static final String REWARD_A1 = "A1";
    private static final String REWARD_B2 = "B2";
    private static final String REWARD_B1 = "B1";
    private static final String REWARD_F = "F";
    private static final JewelType PACHINKO_NEED_JEWEL_TYPE = JewelType.B;
    private static final int PACHINKO_NEED_JEWEL_COUNT = 1;

    private final PachinkoRepository pachinkoRepository;
    private final UserPachinkoRepository userpachinkoRepository;
    private final PachinkoRewardRepository pachinkoRewardRepository;
    private final UserJewelRepository userJewelRepository;
    private final UserRepository userRepository;
    private final DisplayBoardService displayBoardService;
    private final FcmService fcmService;

    @Getter
    private final Set<Integer> selectedSquares = Collections.synchronizedSet(new HashSet<>()); // 스레드 간 동기화 제공

    public Set<Integer> viewSelectedSquares() { // 읽기 전용 뷰 반환
        return Collections.unmodifiableSet(selectedSquares);
    }

    @Getter
    private Long currentRound = 0L;

    public void startFirstRound() {
        assignRewardsToSquares(currentRound);
        System.out.println("첫번쨰 라운드를 시작하기 위해 각 칸에 보상 할당을 완료했습니다.");
    }

    @Transactional
    public List<Integer> getMeChosen(User user) {
        UserPachinko userPachinko = userpachinkoRepository.findByUserAndRound(user, currentRound)
                .orElse(UserPachinko.builder()
                        .round(currentRound)
                        .user(user)
                        .square1(0)
                        .square2(0)
                        .square3(0)
                        .build());
        if (userPachinko.getSquare1() == 0) {
            return Collections.nCopies(3, 0);
        } else {
            List<Integer> meChosen = new ArrayList<>(Collections.nCopies(3, 0));
            meChosen.set(0, userPachinko.getSquare1());
            if (userPachinko.getSquare2() != 0) {
                meChosen.set(1, userPachinko.getSquare2());
            }
            if (userPachinko.getSquare3() != 0) {
                meChosen.set(2, userPachinko.getSquare3());
            }
            return meChosen;
        }
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
    public boolean noMoreJewel(User user) {
        UserJewel userJewel = userJewelRepository.findByUserAndJewelType(user, PACHINKO_NEED_JEWEL_TYPE)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_JEWEL_NOT_FOUND));

        return userJewel.getCount() < PACHINKO_NEED_JEWEL_COUNT;
    }

    @Transactional
    public boolean canSelectMore(User user, Long round) {
        // Optional로 조회하여 값이 없으면 true 반환, 있으면 조건에 맞게 처리
        return userpachinkoRepository.findByUserAndRound(user, round)
                .map(UserPachinko::canSelectMore) // 존재할 때 조건에 맞게 처리
                .orElse(true);  // 존재하지 않으면 true 반환
    }

    @Transactional
    public String selectSquare(User user, long currentRound, int squareNumber) {
        // 6*6 안의 칸인지 확인
        validateSquareNumber(squareNumber);

        // 이미 선택된 칸인지 확인
        if (selectedSquares.contains(squareNumber)) {
            System.out.println(selectedSquares + "이미 " + squareNumber + "가 존재합니다.");

            if (isUserSelected(user, currentRound, squareNumber)) {
                System.out.println("본인이 이전에 선택한 칸입니다.");
                return "본인이 이전에 선택한 칸입니다.";
            } else {
                System.out.println("다른 사용자가 이전에 선택한 칸입니다.");
                return "다른 사용자가 이전에 선택한 칸입니다.";
            }
        }

        // 사용자 Pachinko 상태 조회
        UserPachinko userPachinko = userpachinkoRepository.findByUserAndRoundForUpdate(user, currentRound)
                .orElseGet(() -> initializeUserPachinko(user, currentRound));

        // 칸 추가 로직 & 더 이상 선택할 수 없는 경우 처리
        if (!userPachinko.addSquare(squareNumber)) {
            System.out.println("이미 세칸을 선택하셨습니다.");
            return "이미 세개의 칸을 선택하셨습니다.";
        }

        userpachinkoRepository.save(userPachinko);
        System.out.println("user packinko에 선택한 칸인 " + squareNumber + "을 저장했습니다.");

        // 보석 차감 로직
        deductUserJewel(user);
        System.out.println("빠칭코 칸 선택을 위해 b급 보석 하나를 지불하셔서 db에서 보석을 차감했습니다.");

        // 선택한 칸 set에 넣기
        addSelectedSquare(squareNumber);
        System.out.println("선택한 칸을 set에 삽입했습니다. 변경된 set: " + selectedSquares);

        return "정상적으로 선택 완료되었습니다.";
    }

    private boolean isUserSelected(User user, long currentRound, int squareNumber) {
        UserPachinko userPachinko = userpachinkoRepository.findByUserAndRound(user, currentRound)
                .orElseGet(() -> initializeUserPachinko(user, currentRound));
        Integer s1 = userPachinko.getSquare1();
        Integer s2 = userPachinko.getSquare2();
        Integer s3 = userPachinko.getSquare3();
        return (squareNumber == s1 || squareNumber == s2 || squareNumber == s3);
    }

    public synchronized void addSelectedSquare(int square) {
        selectedSquares.add(square);
    }

    private void deductUserJewel(User user) {
        UserJewel userJewel = userJewelRepository.findByUserAndJewelType(user, PACHINKO_NEED_JEWEL_TYPE)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_JEWEL_NOT_FOUND));
        userJewel.decreaseCount(PACHINKO_NEED_JEWEL_COUNT);
        userJewelRepository.save(userJewel);
    }

    private UserPachinko initializeUserPachinko(User user, long currentRound) {
        UserPachinko newUserPachinko = PachinkoConverter.saveUserPachinko(currentRound, user);
        return userpachinkoRepository.save(newUserPachinko);
    }

    private void validateSquareNumber(int squareNumber) {
        if (squareNumber < MIN_PACHINKO_SQUARE_NUMBER || squareNumber > TOTAL_PACHINKO_SQUARE_COUNT) {
            throw new GeneralException(ErrorCode.PACHINKO_OUT_OF_BOUND);
        }
    }

    public boolean isGameOver() {
        System.out.println("모든 칸 선택 되었나 확인중");
        return (selectedSquares.size() == TOTAL_PACHINKO_SQUARE_COUNT);
    }

    @Transactional
    public void giveRewards() throws IOException {
        System.out.println("보상 전달 시작");
        List<UserPachinko> userPachinkoList = userpachinkoRepository.findByRound(currentRound);

        for (UserPachinko userPachinko : userPachinkoList) {
            User user = userPachinko.getUser();
            user.updatePreviousPachinkoRound(currentRound);
            userRepository.save(user);

            List<Integer> squares = new ArrayList<>();
            squares.add(userPachinko.getSquare1());
            squares.add(userPachinko.getSquare2());
            squares.add(userPachinko.getSquare3());

            for (int i = 0; i < 3; i++) {
                if (squares.get(i) > 0) {
                    int sq = squares.get(i);
                    // 해당 칸에 대한 보상 알아내기
                    System.out.println("보상찾기 - squares.get(i): " + squares.get(i));
                    Pachinko pa = pachinkoRepository.findByRoundAndSquare(currentRound, sq)
                            .orElseThrow(() -> new GeneralException(ErrorCode.BAD_REQUEST));

                    if (pa.getJewelType() != JewelType.F) {
                        UserJewel uj = userJewelRepository.findByUserAndJewelType(user, pa.getJewelType())
                                .orElseThrow(() -> new GeneralException(ErrorCode.BAD_REQUEST));
                        uj.setCount(pa.getJewelNum());
                        userJewelRepository.save(uj);
                        System.out.println("user jewel 보상에 따라 갱신완료");
                    }

                    if (pa.getJewelType() == JewelType.S) {
                        displayBoardService.addPachinkoSJewelMessage(user);
                    }

                    List<UserDeviceToken> userDeviceTokens = user.getDeviceTokenList();
                    for (UserDeviceToken userDeviceToken : userDeviceTokens) {
                        FcmSimpleReqDto requestDTO = FcmConverter.toFcmSimpleReqDto(userDeviceToken.getDeviceToken(),
                                Constant.FCM_PACHINKO_GAME_FINISH_TITLE, Constant.FCM_PACHINKO_GAME_FINISH_BODY);
                        fcmService.sendMessageTo(requestDTO);
                    }
                }
            }
            System.out.println("보상 전달 완료");
        }
    }

    public void assignRewardsToSquares(Long currentRound) {
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

        int fSquareCount =
                TOTAL_PACHINKO_SQUARE_COUNT - (s1.getSquareCount() + a1.getSquareCount() + b2.getSquareCount()
                        + b1.getSquareCount());

        for (int i = 0; i < s1.getSquareCount(); i++) {
            rewards.add(REWARD_S1);
        }
        for (int i = 0; i < a1.getSquareCount(); i++) {
            rewards.add(REWARD_A1);
        }
        for (int i = 0; i < b2.getSquareCount(); i++) {
            rewards.add(REWARD_B2);
        }
        for (int i = 0; i < b1.getSquareCount(); i++) {
            rewards.add(REWARD_B1);
        }
        for (int i = 0; i < fSquareCount; i++) {
            rewards.add(REWARD_F);
        }

        SecureRandom secureRandom = new SecureRandom();
        Collections.shuffle(rewards, secureRandom);

        // db에 넣기
        for (int i = 0; i < TOTAL_PACHINKO_SQUARE_COUNT; i++) {
            JewelType jewelType;
            int jewelNum;
            if (Objects.equals(rewards.get(i), REWARD_S1)) {
                jewelType = JewelType.S;
                jewelNum = 1;
            } else if (Objects.equals(rewards.get(i), REWARD_A1)) {
                jewelType = JewelType.A;
                jewelNum = 1;
            } else if (Objects.equals(rewards.get(i), REWARD_B2)) {
                jewelType = JewelType.B;
                jewelNum = 2;
            } else if (Objects.equals(rewards.get(i), REWARD_B1)) {
                jewelType = JewelType.B;
                jewelNum = 1;
            } else {
                jewelType = JewelType.F;
                jewelNum = 0;
            }

            Pachinko newPachinco = PachinkoConverter.savePachinko(currentRound, i + 1, jewelType, jewelNum);
            pachinkoRepository.save(newPachinco);
            System.out.println("각각의 칸에 보상 설정 완료");
        }
    }

    @Transactional
    public List<Long> getRewards(User user) {
        Long round = user.getPreviousPachinkoRound();
        if (round == -1) {
            throw new GeneralException(ErrorCode.PACHINKO_NO_PREVIOUS_ROUND);
        }

        UserPachinko userPachinko = userpachinkoRepository.findByUserAndRound(user, round)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_PACHINKO_NOT_FOUND));

        List<Integer> squares = new ArrayList<>();
        squares.add(userPachinko.getSquare1());
        squares.add(userPachinko.getSquare2());
        squares.add(userPachinko.getSquare3());

        List<Long> jewelsNum = new ArrayList<>(Collections.nCopies(3, 0L));

        for (int i = 0; i < 3; i++) {
            if (squares.get(i) > 0) {
                int sq = squares.get(i);
                Pachinko pa = pachinkoRepository.findByRoundAndSquare(round, sq)
                        .orElseThrow(() -> new GeneralException(ErrorCode.BAD_REQUEST));
                if (pa.getJewelType() == JewelType.S) {
                    jewelsNum.set(0, jewelsNum.get(0) + pa.getJewelNum());
                } else if (pa.getJewelType() == JewelType.A) {
                    jewelsNum.set(1, jewelsNum.get(1) + pa.getJewelNum());
                } else if (pa.getJewelType() == JewelType.B) {
                    jewelsNum.set(2, jewelsNum.get(2) + pa.getJewelNum());
                }
            }
        }

        return jewelsNum;
    }

    public List<Pachinko> getPreviousPachinkoRewards(Long round) {
        return pachinkoRepository.findByRound(round);
    }

    @PostConstruct
    public Long updateSelectedSquaresSet() {

        if (selectedSquares.size() == TOTAL_PACHINKO_SQUARE_COUNT) {
            currentRound = userpachinkoRepository.findCurrentRound() + 1;
            selectedSquares.clear();
        } else {
            selectedSquares.clear();
            currentRound = userpachinkoRepository.findCurrentRound();

            List<UserPachinko> userPachinkoList = userpachinkoRepository.findByRound(currentRound);
            for (UserPachinko userPachinko : userPachinkoList) {
                selectedSquares.add(userPachinko.getSquare1());
                selectedSquares.add(userPachinko.getSquare2());
                selectedSquares.add(userPachinko.getSquare3());
            }
            selectedSquares.remove(0);
        }

        return currentRound;
    }

}
