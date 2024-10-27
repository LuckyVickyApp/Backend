package LuckyVicky.backend.pachinko.domain;

import LuckyVicky.backend.enhance.domain.JewelType;
import jakarta.persistence.*;
import lombok.*;

// 특정 게임의 특정 칸에 대한 보상
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "pachinko", uniqueConstraints = { @UniqueConstraint(columnNames = {"round", "square"}) })
public class Pachinko {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long round; // 게임 번호

    private Integer square; // 칸 번호

    private Integer jewelNum; // 보석 개수

    @Enumerated(EnumType.STRING)
    private JewelType jewelType; // 보석 종류

}
