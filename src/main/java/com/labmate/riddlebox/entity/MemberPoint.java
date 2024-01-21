package com.labmate.riddlebox.entity;


import com.labmate.riddlebox.enumpackage.FaqCategory;
import com.labmate.riddlebox.enumpackage.GameStatus;
import com.labmate.riddlebox.enumpackage.InquiryStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPoint extends BaseEntity {

    @Id
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  //게임기록 인조식별자

    private String reason;  //적립 이유
    private int earned_points;  //적립포인트
    private LocalDateTime earned_date;  //적립일
    private int total_points;  //누적포인트


    /*   생성자   */
    public MemberPoint(Member member, String reason, int earnedPoints, LocalDateTime earnedDate, int totalPoints) {
        this.member = member;
        this.reason = reason;
        this.earned_points = earnedPoints;
        this.earned_date = earnedDate;
        this.total_points = totalPoints;
    }

}
