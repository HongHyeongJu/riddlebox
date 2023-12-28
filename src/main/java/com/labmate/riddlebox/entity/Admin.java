package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.InquiryStatus;
import com.labmate.riddlebox.enumpackage.UserRole;
import com.labmate.riddlebox.enumpackage.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//기본 생성자를 제공하며, 이 생성자의 접근 수준을 protected로 설정하는 것을 의미(JPA 스펙에 따르면, 모든 엔티티는 기본 생성자를 가지고 있어야함)
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;  //관리자번호

    private String username;  //이름
    private String loginId;  //로그인 ID
    private String password;  //비밀번호
    private String email;  //이메일주소

    @Enumerated(EnumType.STRING)
    private UserRole role;  //권한이름

    private String nickname;  //닉네임
    private LocalDateTime regDate;  //가입일

    @Enumerated(EnumType.STRING)
    private UserStatus status;  //계정 상태

    @OneToMany(mappedBy = "inquiry")
    private List<Inquiry> inquiries = new ArrayList<>();


    /*   생성자   */
    public Admin(String username, String loginId, String password, String email, UserRole role, String nickname) {
        this.username = username;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.role = role;
        this.nickname = nickname;
        this.regDate = LocalDateTime.now(); // 현재 시간으로 설정
        this.status = UserStatus.ACTIVE; // 기본 상태 설정
    }


    /*    변경 메서드    */
    //관리자 정보 변경 메서드
    public void updateAdminInfo(String newUsername, String newPassword, String newNickname) {
        if (!isValidNickname(nickname)) {
            throw new IllegalArgumentException("중복 닉네임");
        }
        this.username = newUsername;
        this.password = newPassword;
        this.nickname = newNickname;
    }

    //닉네임 유효성 검증 메서드
    private boolean isValidNickname(String newNickname) {
        //중복검증
        return true;
    }

    //관리자 권한 변경 메서드
    public void updateAdminRole(UserRole newRole) {
        this.role = newRole;
    }

    //상태 변경
    public void changeStatus(UserStatus newStatus) {
        this.status = newStatus;
    }

    //삭제
    public void softDelete() {
        changeStatus(UserStatus.DELETED);
    }


}




