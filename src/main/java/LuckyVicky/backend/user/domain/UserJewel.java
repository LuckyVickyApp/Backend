package LuckyVicky.backend.user.domain;

import LuckyVicky.backend.enhance.domain.JewelType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user_jewel")
public class UserJewel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 보유 개수
    private Integer count;

    // 보석 종류
    private JewelType jewelType;

    public void updateCount(int num){
        this.count += num;
    }
}
