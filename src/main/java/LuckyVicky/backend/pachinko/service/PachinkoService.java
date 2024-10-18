package LuckyVicky.backend.pachinko.service;

import LuckyVicky.backend.pachinko.repository.PachinkoRepository;
import LuckyVicky.backend.pachinko.repository.UserPachinkoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

// 빠칭코 비즈니스 로직
@Slf4j
@Service
@RequiredArgsConstructor
public class PachinkoService {

    private final PachinkoRepository pachinkoRepository;
    private final UserPachinkoRepository userpachinkoRepository;
    private Set<Integer> selectedSquares = new HashSet<>();

    public boolean selectSquare(int squareNumber) {
        // 6*6 안의 칸인지 확인
        if (squareNumber < 1 || squareNumber > 36) return false;
        // 이미 선택된 칸인지 확인
        if (selectedSquares.contains(squareNumber)) return false;
        // 선택되지 않은 칸이면 추가
        selectedSquares.add(squareNumber);

        return true;
    }

    public Set<Integer> getSelectedSquares() {
        return selectedSquares;
    }

    // 필요한 게임 로직들 추가

}
