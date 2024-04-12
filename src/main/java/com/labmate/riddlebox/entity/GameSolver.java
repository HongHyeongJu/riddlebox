package com.labmate.riddlebox.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSolver extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gamesolver_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RBUser user; // 게임을 해결한 사용자

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;  //해결한 게임 1:1

    private LocalDateTime endDateTime;  //종료일시
    private LocalDateTime solvedDateTime; // 게임 해결 일시

    private Long remainingTime; // 남은 시간 (예: 초 단위)

    public GameSolver(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setUser(RBUser user) {
        this.user = user;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setGameSolveUser(RBUser user) {
        this.user = user;

        // endDateTime과 현재 시간 사이의 Duration을 계산
        Duration duration = Duration.between(LocalDateTime.now(), this.endDateTime);
        // Duration을 초 단위로 변환하여 remainingTime에 할당
        this.remainingTime = duration.getSeconds();
    }

}
