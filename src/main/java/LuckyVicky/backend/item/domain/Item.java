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
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private LocalDate availableDate;

    @Column(nullable = true)
    private String quantity;

    @Column(nullable = true)
    private String imageUrl;

    public void updateItem(String name, String description, String availableDate, String quantity, String imageUrl) {
        this.name = name;
        this.description = description;
        this.availableDate = availableDate != null ? LocalDate.parse(availableDate) : this.availableDate;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }
}
