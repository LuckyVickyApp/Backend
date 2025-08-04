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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PachinkoService {
    private static final int PACHINKO_TOTAL_SQUARE_COUNT = 36;
    private static final int PACHINKO_MIN_SQUARE_NUMBER = 1;
    public static final int PACHINKO_USER_MAX_SQUARES = 10;
    private static final String REWARD_S1 = "S1";
    private static final String REWARD_A1 = "A1";
    private static final String REWARD_B2 = "B2";
    private static final String REWARD_B1 = "B1";
    private static final String REWARD_F = "F";
    private static final JewelType PACHINKO_NEED_JEWEL_TYPE = JewelType.B;
    private static final int PACHINKO_NEED_JEWEL_COUNT = 1;

    private final PachinkoRepository pachinkoRepository;
    private final UserPachinkoRepository userPachinkoRepository;
    private final PachinkoRewardRepository pachinkoRewardRepository;
    private final UserJewelRepository userJewelRepository;
    private final UserRepository userRepository;
    private final DisplayBoardService displayBoardService;
    private final FcmService fcmService;

    @Getter
    private final Set<Integer> selectedSquares = ConcurrentHashMap.newKeySet();

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
        List<UserPachinko> selected = userPachinkoRepository.findByUserAndRound(user, currentRound);
        return selected.stream()
                .map(UserPachinko::getSquare)
                .sorted()
                .collect(Collectors.toList());
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
        return userPachinkoRepository.countByUserAndRound(user, round) < PACHINKO_USER_MAX_SQUARES;
    }

    @Transactional
    @Retryable(
            value = DataIntegrityViolationException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Synchronized
    public String selectSquare(User user, int squareNumber) {
        // 칸 번호 유효성 검증
        validateSquareNumber(squareNumber);

        // DB 확인
        if (userPachinkoRepository.existsByRoundAndSquare(currentRound, squareNumber)) {
            return "이미 선택된 칸 입니다.";
        }

        // DB 갱신
        userPachinkoRepository.save(PachinkoConverter.saveUserPachinko(user, currentRound, squareNumber));
        log.info("user pachinko에 선택한 칸인 {}을 저장했습니다.", squareNumber);

        // 보석 차감
        deductUserJewel(user);
        log.info("빠칭코 칸 선택을 위해 B급 보석 하나를 지불하여 DB에서 보석을 차감했습니다.");

        return "정상적으로 선택 완료되었습니다.";
    }

    public void addSelectedSquare(int square) {
        selectedSquares.add(square);
    }

    private void deductUserJewel(User user) {
        UserJewel userJewel = userJewelRepository.findByUserAndJewelType(user, PACHINKO_NEED_JEWEL_TYPE)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_JEWEL_NOT_FOUND));
        userJewel.decreaseCount(PACHINKO_NEED_JEWEL_COUNT);
        userJewelRepository.save(userJewel);
    }

    private void validateSquareNumber(int squareNumber) {
        if (squareNumber < PACHINKO_MIN_SQUARE_NUMBER || squareNumber > PACHINKO_TOTAL_SQUARE_COUNT) {
            throw new GeneralException(ErrorCode.PACHINKO_OUT_OF_BOUND);
        }
    }

    public boolean isGameOver() {
        System.out.println("모든 칸 선택 되었나 확인중");
        return (selectedSquares.size() == PACHINKO_TOTAL_SQUARE_COUNT);
    }

    @Transactional
    public void giveRewards() {
        System.out.println("보상 전달 시작");
        List<UserPachinko> userPachinkoList = userPachinkoRepository.findByRoundWithUserAndDeviceTokens(currentRound);

        ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<?>> futures = new ArrayList<>();

        for (UserPachinko userPachinko : userPachinkoList) {
            futures.add(virtualThreadExecutor.submit(() -> {
                try {
                    User user = userPachinko.getUser();
                    user.updatePreviousPachinkoRound(currentRound);
                    userRepository.save(user);

                    int sq = userPachinko.getSquare();

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

                    for (UserDeviceToken token : user.getDeviceTokenList()) {
                        FcmSimpleReqDto dto = FcmConverter.toFcmSimpleReqDto(
                                token.getDeviceToken(),
                                Constant.FCM_PACHINKO_GAME_FINISH_TITLE,
                                Constant.FCM_PACHINKO_GAME_FINISH_BODY
                        );
                        fcmService.sendMessageTo(dto);
                    }

                    System.out.println("보상 전달 완료");
                } catch (Exception e) {
                    log.error("보상 작업 처리 중 예외 발생", e);
                }
            }));
        }

        // 모든 작업 완료 대기
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("보상 작업 처리 중 예외 발생", e);
            }
        }

        virtualThreadExecutor.shutdown();
        System.out.println("모든 보상 전달 완료");
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
                PACHINKO_TOTAL_SQUARE_COUNT - (s1.getSquareCount() + a1.getSquareCount() + b2.getSquareCount()
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
        for (int i = 0; i < PACHINKO_TOTAL_SQUARE_COUNT; i++) {
            JewelType jewelType;
            int jewelNum;
            switch (rewards.get(i)) {
                case REWARD_S1 -> {
                    jewelType = JewelType.S;
                    jewelNum = 1;
                }
                case REWARD_A1 -> {
                    jewelType = JewelType.A;
                    jewelNum = 1;
                }
                case REWARD_B2 -> {
                    jewelType = JewelType.B;
                    jewelNum = 2;
                }
                case REWARD_B1 -> {
                    jewelType = JewelType.B;
                    jewelNum = 1;
                }
                case null, default -> {
                    jewelType = JewelType.F;
                    jewelNum = 0;
                }
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

        // 유저가 해당 라운드에 선택한 모든 square 조회
        List<UserPachinko> selections = userPachinkoRepository.findByUserAndRound(user, round);
        if (selections.isEmpty()) {
            throw new GeneralException(ErrorCode.USER_PACHINKO_NOT_FOUND);
        }

        // A, B, S 보석 수를 담을 리스트
        List<Long> jewelsNum = new ArrayList<>(List.of(0L, 0L, 0L)); // [S, A, B]

        for (UserPachinko selection : selections) {
            int sq = selection.getSquare();
            Pachinko pa = pachinkoRepository.findByRoundAndSquare(round, sq)
                    .orElseThrow(() -> new GeneralException(ErrorCode.BAD_REQUEST));

            switch (pa.getJewelType()) {
                case S -> jewelsNum.set(0, jewelsNum.get(0) + pa.getJewelNum());
                case A -> jewelsNum.set(1, jewelsNum.get(1) + pa.getJewelNum());
                case B -> jewelsNum.set(2, jewelsNum.get(2) + pa.getJewelNum());
            }
        }

        return jewelsNum;
    }


    public List<Pachinko> getPreviousPachinkoRewards(Long round) {
        return pachinkoRepository.findByRound(round);
    }

    @PostConstruct
    public Long updateSelectedSquaresSet() {

        if (selectedSquares.size() == PACHINKO_TOTAL_SQUARE_COUNT) {
            currentRound = userPachinkoRepository.findCurrentRound() + 1;
            selectedSquares.clear();
        } else {
            selectedSquares.clear();
            currentRound = userPachinkoRepository.findCurrentRound();

            List<UserPachinko> userPachinkoList = userPachinkoRepository.findByRound(currentRound);
            for (UserPachinko userPachinko : userPachinkoList) {
                selectedSquares.add(userPachinko.getSquare());
            }
            selectedSquares.remove(0);
        }

        return currentRound;
    }

}
