package com.labmate.riddlebox.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPoint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_point_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private RBUser user;

    private String reason;  //적립 이유
    private int earnedPoints;  //적립포인트
    private LocalDateTime earnedDate;  //적립일
    private int totalPoints;  //누적포인트

    // 생성자
    public UserPoint(RBUser user, String reason, int earnedPoints, LocalDateTime earnedDate, int totalPoints) {
        this.user = user;
        this.reason = reason;
        this.earnedPoints = earnedPoints;
        this.earnedDate = earnedDate;
        this.totalPoints = totalPoints;
    }

    public void setUser(RBUser user) {
        this.user = user;
    }




}
