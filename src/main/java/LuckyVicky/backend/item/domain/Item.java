package LuckyVicky.backend.item.domain;

import LuckyVicky.backend.enhance.domain.EnhanceItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String price;

    private String descriptionKey;

    private String descriptionValue;

    // 상품 강화 시작일
    @Column(nullable = false)
    private LocalDate enhanceStartDate;

    // 상품 강화 종료일
    @Column(nullable = false)
    private LocalDate enhanceEndDate;

    @Column(nullable = true)
    private Integer quantity;

    @Column(nullable = true)
    private String imageUrl;

    private Integer likeCount;

    @OneToMany(mappedBy = "item")
    private List<ItemLike> itemLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    private List<EnhanceItem> enhanceItemList = new ArrayList<>();

    public Integer increaseLikeCount() {
        this.likeCount += 1;
        return this.likeCount;
    }

    public Integer decreaseLikeCount() {
        this.likeCount -= 1;
        return this.likeCount;
    }
}
