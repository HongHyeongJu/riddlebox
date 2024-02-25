package com.labmate.riddlebox.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.benmanes.caffeine.cache.Expiry;
import com.labmate.riddlebox.enumpackage.UserRole;
import com.labmate.riddlebox.enumpackage.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class RBUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;  //회원번호

    @Column(nullable = false, unique = true)
    private String loginEmail;  //이메일, 궁극적으로 loginId가 됨

    private String name;  //실명
    private String nickname;  //닉네임

    @JsonIgnore
    @Column(nullable = false)
    private String password;  //비밀번호
    @Column(nullable = false)
    private LocalDate passwordSetDate; // 비밀번호 설정일


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RBRole> roles = new HashSet<>();  //역할

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialProfile> socialProfiles = new ArrayList<>();

    private LocalDateTime regDate;  //가입일
    private LocalDateTime lastLoginDate;  //마지막 방문일

    @Enumerated(EnumType.STRING)
    private UserStatus status;  //계정 상태

    @OneToMany(mappedBy = "user")
    private List<GameRecord> gameRecords = new ArrayList<>();

    @OneToMany(mappedBy = "inquirer")
    private List<Inquiry> inquiries = new ArrayList<>();

    @OneToMany(mappedBy = "responder")
    private List<Inquiry> responses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<GameEvent> gameEvents = new ArrayList<>();

    /*   생성자   */

    public RBUser(String loginEmail, String name, String nickname, String password,
                  LocalDate passwordSetDate, LocalDateTime regDate, LocalDateTime lastLoginDate,
                  UserStatus status) {
        this.loginEmail = loginEmail;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.passwordSetDate = passwordSetDate;
        this.regDate = regDate;
        this.lastLoginDate = lastLoginDate;
        this.status = status;
    }

    public void addSocialProfile(SocialProfile socialProfile) {
        socialProfiles.add(socialProfile);
        socialProfile.setUser(this);
    }

    public void removeSocialProfile(SocialProfile socialProfile) {
        socialProfiles.remove(socialProfile);
        socialProfile.setUser(null);
    }

    // 관계 설정 및 수정을 위한 헬퍼 메소드
    public void addGameRecord(GameRecord gameRecord) {
        this.gameRecords.add(gameRecord);
        gameRecord.setUser(this);
    }

    public void addInquiry(Inquiry inquiry) {
        this.inquiries.add(inquiry);
        inquiry.setInquirer(this);
    }

    public void addResponse(Inquiry response) {
        this.responses.add(response);
        response.setResponder(this);
    }

    public void addGameEvent(GameEvent gameEvent) {
        this.gameEvents.add(gameEvent);
        gameEvent.setUser(this);
    }

    /*    변경 메서드    */
    //사용자 정보 변경 메서드
    public void updateMemberInfo(String newPassword, String newNickname) {
        if (!isValidNickname(newNickname)) {
            throw new IllegalArgumentException("중복 닉네임");
        }
        this.password = newPassword;
        this.nickname = newNickname;
    }

    //닉네임 유효성 검증 메서드
    private boolean isValidNickname(String newNickname) {
        //중복검증 todo :

        return true;
    }

/*    //역할 변경 메서드
    public void updateMemberRole(RBRole newRole) {
        //변경하려는 사람의 역할이 Director이어야 함

    }*/

    //마지막 로그인 변경 메서드
    public void updateLastLoginDate() {
        this.lastLoginDate = LocalDateTime.now();
    }

    //상태 변경
    public void changeStatus(UserStatus newStatus) {
        this.status = newStatus;
    }

    //삭제
    public void softDelete() {
        changeStatus(UserStatus.DISABLED);
    }

}