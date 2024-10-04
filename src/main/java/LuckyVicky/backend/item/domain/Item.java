package LuckyVicky.backend.item.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "item")  // 테이블 이름을 명시
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private LocalDate availableDate;  // 날짜를 LocalDate로 저장

    @Column(nullable = true)
    private String quantity;  // 수량을 문자열로 처리

    @Column(nullable = true)
    private String imageUrl;  // 이미지 URL 저장
}
