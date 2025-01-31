package LuckyVicky.backend.invitation.domain;

import LuckyVicky.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "invitation")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id") // 초대자 (= 코드 주인)
    private User owner;

    @Column(unique = true)
    private String writer; // 초대 수락자 (= 코드 입력자)
}
