package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.UserRole;
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
    private UserStatus status;  //계정 상태

    @ManyToMany(mappedBy = "roles")
    private Set<RBUser> users = new HashSet<>();

    /*   생성자   */
    public RBRole(String name, String description, Integer level, UserStatus status, Set<RBUser> users) {
        this.name = name;
        this.description = description;
        this.level = level;
        this.status = status;
        this.users = users;
    }
}