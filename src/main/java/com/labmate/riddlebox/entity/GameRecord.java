package com.labmate.riddlebox.entity;


import com.labmate.riddlebox.enumpackage.GameResultType;
import com.labmate.riddlebox.enumpackage.InquiryStatus;
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
public class GameRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_record_id")
    private Long id;  //게임기록 인조식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  //회원번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;  //게임번호

    private int score;  //점수
    private int playTime;  //플레이타임(초?? 이 기준은 이후에)
    private float successRate;  //정답률

    @Enumerated(EnumType.STRING)
    private GameResultType resultType;  // 게임 결과 타입


    /*    생성자    */
    public GameRecord(Member member, Game game, int score, int playTime, float successRate, GameResultType resultType) {
        this.member = member;
        this.game = game;
        this.score = score;
        this.playTime = playTime;
        this.successRate = successRate;
        this.resultType = resultType;
    }

}
