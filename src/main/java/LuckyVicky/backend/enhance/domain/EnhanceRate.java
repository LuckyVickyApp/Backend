package LuckyVicky.backend.enhance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "enhance_rate")
public class EnhanceRate {
    // 식별자
    @Id
    private Long id;

    // 강화 시작 레벨
    private Integer enhanceLevel;

    // 강화 시도 보석 종류
    private JewelType jewelType;

    // 상승 확률
    @Column(precision = 3, scale = 2)
    private BigDecimal upRate;

    // 하락 확률
    @Column(precision = 3, scale = 2)
    private BigDecimal downRate;

    // 유지 확률
    @Column(precision = 3, scale = 2)
    private BigDecimal keepRate;

    // 파괴 확률
    @Column(precision = 3, scale = 2)
    private BigDecimal destroyRate;

}
