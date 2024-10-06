package LuckyVicky.backend.enhance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

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
    private Double upRate;

    // 하락 확률
    private Double downRate;

    // 유지 확률
    private Double keepRate;

    // 파괴 확률
    private Double destroyRate;

}
