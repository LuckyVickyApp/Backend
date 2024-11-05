package LuckyVicky.backend.attendance.domain;

import LuckyVicky.backend.enhance.domain.JewelType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance_reward")
public class AttendanceReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int day;  // 출석 일차

    @Column(nullable = false)
    private String rewardMessage;

    @Enumerated(EnumType.STRING)
    private JewelType jewelType;

    @Column(nullable = false)
    private int jewelCount;
}
