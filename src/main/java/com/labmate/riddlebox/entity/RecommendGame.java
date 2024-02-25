package com.labmate.riddlebox.entity;


import com.labmate.riddlebox.enumpackage.GameStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendGame extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommend_id")
    private Long id;  //추천게임테이블 인조식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;  //추천할 게임번호

    private String reason;  //추천이유
    private LocalDateTime startDate;  //추천시작일
    private LocalDateTime endDate;  //추천종료일

    @Enumerated(EnumType.STRING)
    private GameStatus status;  //상태

    private int recommendRank;  //추천순위
    private int clicks;  //클릭수



    /*   생성자   */
    public RecommendGame(String reason, LocalDateTime startDate,
                         LocalDateTime endDate, int recommendRank) {
        this.reason = reason;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = GameStatus.ACTIVE; // 초기 상태 설정
        this.recommendRank = recommendRank;
        this.clicks = 0; // 클릭수 초기화
    }


    public void setGame(Game game) {
        if (this.game != null) {
            this.game.getRecommendGames().remove(this);
        }
        this.game = game;
    }



    /*    변경 메서드    */
    //추천 게임 상태 변경하기
    public void changeStatus(GameStatus newStatus) {
        this.status = newStatus;
    }

    //추천 내용수정 메서드
    public void updateRecommendation(String reason, LocalDateTime startDate,
                                 LocalDateTime endDate, GameStatus newStatus,
                                 int newRecommendRank, int newClicks) {
    this.reason = reason;
    this.startDate = startDate;
    this.endDate = endDate;
    this.status = newStatus;
    this.recommendRank = newRecommendRank;
    this.clicks = newClicks;
}

    //삭제
    public void softDelete() {
        changeStatus(GameStatus.DELETED);
    }

    //클릭수 증가
    public void addClicks() {
        this.clicks++;
    }
}
