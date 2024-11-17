package LuckyVicky.backend.pachinko.domain;

import LuckyVicky.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user_pachinko")
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

    public void setSquares(int square1, int square2, int square3){
        this.square1 = square1;
        this.square2 = square2;
        this.square3 = square3;
    }

    public boolean canSelectMore() {
        return square3 == null || square3 == 0; // 세 번째 칸이 비어 있으면 선택 가능
    }
}
