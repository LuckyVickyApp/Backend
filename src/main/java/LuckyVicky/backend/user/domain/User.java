package LuckyVicky.backend.user.domain;

import LuckyVicky.backend.enhance.domain.EnhanceItem;
import LuckyVicky.backend.global.entity.BaseEntity;
import LuckyVicky.backend.invitation.domain.Invitation;
import LuckyVicky.backend.pachinko.domain.UserPachinko;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String provider;

    private String sex;

    private String birth;

    @Column(nullable = false)
    private String signInDate;

    private String recipientName;

    private String streetAddress;

    private String detailedAddress;

    private String phoneNumber;

    private String profileImage;

    @Column(nullable = false)
    private Integer attendanceDate;

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

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateAddress(String streetAddress, String detailedAddress) {
        this.streetAddress = streetAddress;
        this.detailedAddress = detailedAddress;
    }

    public void updatePreviousPachinkoRound(Long round) {
        this.previousPachinkoRound = round;
    }

}
