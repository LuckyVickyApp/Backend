package LuckyVicky.backend.pachinko.domain;

import LuckyVicky.backend.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        name = "user_pachinko",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "round"})
)
public class UserPachinko {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long round; // 게임 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer square1;

    private Integer square2;

    private Integer square3;

    public void setSquares(int square1, int square2, int square3) {
        this.square1 = square1;
        this.square2 = square2;
        this.square3 = square3;
    }

    public boolean addSquare(int squareNumber) {
        if (square1 == null || square1 == 0) {
            square1 = squareNumber;
            return true;
        } else if (square2 == null || square2 == 0) {
            square2 = squareNumber;
            return true;
        } else if (square3 == null || square3 == 0) {
            square3 = squareNumber;
            return true;
        }
        return false;
    }

    public boolean canSelectMore() {
        return square3 == null || square3 == 0; // 세 번째 칸이 비어 있으면 선택 가능
    }
}
