package LuckyVicky.backend.user.domain;

import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.global.entity.BaseEntity;
import LuckyVicky.backend.invitation.domain.Invitation;
import LuckyVicky.backend.pachinko.domain.UserPachinko;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user")
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String provider;

//    @Column(nullable = false)
    private String sex;

//    @Column(nullable = false)
    private String birth;

    @Column(nullable = false)
    private String signInDate;

    private String address;

    private String phoneNumber;

    private String profileImage;

    @Column(nullable = false)
    private Integer attendanceDate;

    @Column
    private LocalDate lastAttendanceDate;

    @Column(nullable = false, unique = true)
    private String inviteCode;

    @Column(nullable = false)
    private Long previousPachinkoRound;

    @Setter
    @Column(nullable = false)
    private LocalDateTime rouletteAvailableTime;

    @Column(nullable = false)
    private Integer advertiseTodayLeftNum;

    @OneToMany(mappedBy = "user")
    private List<UserJewel> userJewelList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<EnhanceItem> enhanceItemList = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Invitation> acceptorList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserPachinko> userPachinkoList = new ArrayList<>();

    public User(String username, String nickname, String email, String provider) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.provider = provider;
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImage){
        this.profileImage = profileImage;
    }

    public void updateAddress(String address) { this.address = address; }

    public void updatePreviousPachinkoRound(Long round){
        this.previousPachinkoRound = round;
    }

    public void setRouletteAvailableTime(LocalDateTime nextAvailableTime) {
        this.rouletteAvailableTime = nextAvailableTime;
    }

    public void setAttendanceDate(Integer attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

}
