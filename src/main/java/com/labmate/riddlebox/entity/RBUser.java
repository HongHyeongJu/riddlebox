package com.labmate.riddlebox.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.benmanes.caffeine.cache.Expiry;
import com.labmate.riddlebox.enumpackage.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private String password;  //비밀번호
    private LocalDate passwordSetDate; // 비밀번호 설정일

    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialProfile> socialProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPoint> userPoints = new ArrayList<>();


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
    @Builder
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


    // 관계 설정 및 수정을 위한 헬퍼 메소드
    // UserRole/SocialProfile/GameRecord/
    // Inquiry(inquirer)/Inquiry(responder)/GameEvent

    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
        userRole.setUser(this);
    }

    public void removeUserRole(UserRole userRole) {
        this.userRoles.remove(userRole);
        userRole.setUser(null);
    }


    public void addSocialProfile(SocialProfile socialProfile) {
        this.socialProfiles.add(socialProfile);
        socialProfile.setUser(this);
    }

    public void removeSocialProfile(SocialProfile socialProfile) {
        this.socialProfiles.remove(socialProfile);
        socialProfile.setUser(null);
    }

    public void addGameRecord(GameRecord gameRecord) {
        this.gameRecords.add(gameRecord);
        gameRecord.setUser(this);
    }

    public void removeGameRecord(GameRecord gameRecord) {
        this.gameRecords.remove(gameRecord);
        gameRecord.setUser(null);
    }

    public void addInquiry(Inquiry inquiry) {
        this.inquiries.add(inquiry);
        inquiry.setInquirer(this);
    }

    public void removeInquiry(Inquiry inquiry) {
        this.inquiries.remove(inquiry);
        inquiry.setInquirer(null);
    }

    public void addResponse(Inquiry response) {
        this.responses.add(response);
        response.setResponder(this);
    }

    public void removeResponse(Inquiry response) {
        this.responses.remove(response);
        response.setResponder(null);
    }

    public void addGameEvent(GameEvent gameEvent) {
        this.gameEvents.add(gameEvent);
        gameEvent.setUser(this);
    }

    public void removeGameEvent(GameEvent gameEvent) {
        this.gameEvents.add(gameEvent);
        gameEvent.setUser(null);
    }


    // UserPoint 리스트에 새로운 포인트를 추가하는 헬퍼 메소드
    public void addUserPoint(UserPoint userPoint) {
        this.userPoints.add(userPoint);
        userPoint.setUser(this);
    }

    // UserPoint 리스트에서 특정 포인트를 제거하는 헬퍼 메소드
    public void removeUserPoint(UserPoint userPoint) {
        this.userPoints.remove(userPoint);
        userPoint.setUser(null);
    }



    public Set<RBRole> getRoles() {
        return this.userRoles.stream()
                             .filter(UserRole::getIsActive) // 활성화된 UserRole만 필터링
                             .map(UserRole::getRole) // UserRole에서 Role 추출
                             .collect(Collectors.toSet());
    }



    /*    변경 메서드    */
    //사용자 정보 변경 메서드
    public void setNickname(String newNickname) {
        this.nickname = newNickname;
    }
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

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