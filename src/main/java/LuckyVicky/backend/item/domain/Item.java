package LuckyVicky.backend.item.domain;

import LuckyVicky.backend.enhance.domain.EnhanceItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = true)
    private String description;

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
