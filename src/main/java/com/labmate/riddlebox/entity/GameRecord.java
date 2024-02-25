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
    @JoinColumn(name = "user_id")
    private RBUser user;  //회원번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;  //게임번호

    private int score;  //점수
    private int playTime;  //플레이타임(초?? 이 기준은 이후에)
    private float successRate;  //정답률

    @Enumerated(EnumType.STRING)
    private GameResultType resultType;  // 게임 결과 타입

    /*    생성자    */
    public GameRecord(RBUser user, Game game, int score, int playTime, float successRate, GameResultType resultType) {
        this.user = user;
        this.game = game;
        this.score = score;
        this.playTime = playTime;
        this.successRate = successRate;
        this.resultType = resultType;
    }

    // RBUser 엔티티와의 관계를 설정하는 메서드
    public void setUser(RBUser user) {
        // 이전에 참조하던 관계를 제거
        if (this.user != null) {
            this.user.getGameRecords().remove(this);
        }

        // 새로운 객체 참조 설정
        this.user = user;

    }

    public void setGame(Game game) {
        if (this.game != null) {
            this.game.getGameRecords().remove(this);
        }
        this.game = game;
    }

}
