package LuckyVicky.backend.enhance.domain;

import LuckyVicky.backend.item.domain.Item;
import LuckyVicky.backend.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "enhance_item")
public class EnhanceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 강화 시도 횟수
    private Integer attemptCount;

    // 현재 랭킹
    private Integer ranking;

    // 현재 강화 레벨
    private Integer enhanceLevel;

    // 현재 강화 레벨 도달 시간
    private LocalDateTime enhanceLevelReachedAt;

    // 수령 가능 여부
    private Boolean isGet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void upEnhanceItem() {
        this.enhanceLevel++;
        this.attemptCount++;
    }

    public void downEnhanceItem() {
        this.enhanceLevel--;
        this.attemptCount++;
    }

    public void keepEnhanceItem() {
        this.attemptCount++;
    }

    public void destroyItem() {
        this.enhanceLevel = 1;
        this.attemptCount++;
    }

    public void updateIsGet(int availableQuantity) {
        this.isGet = this.ranking <= availableQuantity;
    }

    public void updateEnhanceLevelReachedAt() {
        this.enhanceLevelReachedAt = LocalDateTime.now();
    }

    public void updateRanking(Integer rank) {
        this.ranking = rank;
    }
}
