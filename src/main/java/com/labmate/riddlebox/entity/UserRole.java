package com.labmate.riddlebox.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RBUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private RBRole role;

    private LocalDateTime assignedDate;  //역할부여날짜
    private String assignedBy;  //역할 부여한 사람
    private Boolean isActive;   // 활성화 여부
    private String notes;

    public UserRole(RBUser user, RBRole role, LocalDateTime assignedDate, String assignedBy, Boolean isActive, String notes) {
        this.user = user;
        this.role = role;
        this.assignedDate = assignedDate;
        this.assignedBy = assignedBy;
        this.isActive = isActive;
        this.notes = notes;
    }

    public void setUser(RBUser user) {
        // 여기서는 기존 user의 userRoles 컬렉션을 조작할 필요가 없습니다.
        this.user = user;
    }

    public void setRole(RBRole role) {
        // 여기서는 기존 role의 userRoles 컬렉션을 조작할 필요가 없습니다.
        this.role = role;
    }


}
