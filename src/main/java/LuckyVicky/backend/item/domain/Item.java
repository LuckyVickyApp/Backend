package LuckyVicky.backend.item.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(nullable = true)
    private LocalDate enhanceStartDate;

    @Column(nullable = true)
    private String quantity;

    @Column(nullable = true)
    private String imageUrl;

    private Integer likeCount;

    @OneToMany(mappedBy = "item")
    private List<ItemLike> itemLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    private List<UserItem> userItemList = new ArrayList<>();

    public Integer increaseLikeCount() {
        this.likeCount += 1;
        return this.likeCount;
    }

    public Integer decreaseLikeCount() {
        this.likeCount -= 1;
        return this.likeCount;
    }
}
