package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.RoleStatus;
import com.labmate.riddlebox.enumpackage.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "roles")
public class RBRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;  //역할 PK

    @Column(nullable = false, unique = true)
    private String name;  //역할 이름

    @Column(length = 500)
    private String description;

    private Integer level;

    @Enumerated(EnumType.STRING)
    private RoleStatus  status;  //역할 활성화 상태

    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = new HashSet<>();

    /*   생성자   */
    public RBRole(String name, String description, Integer level, RoleStatus status) {
        this.name = name;
        this.description = description;
        this.level = level;
        this.status = status;
    }

    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
        userRole.setRole(this); // 올바르게 UserRole에 현재 Role을 설정합니다.
    }

    public void removeUserRole(UserRole userRole) {
        this.userRoles.remove(userRole); // 여기서 제거 작업을 수행해야 합니다.
        userRole.setRole(null); // UserRole에서 Role 참조를 제거합니다.
    }



}