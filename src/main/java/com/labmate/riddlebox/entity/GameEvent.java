package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.EventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameEvent extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RBUser user;  //회원번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;  //게임번호

    private LocalDateTime eventTime;

    @Enumerated(EnumType.STRING)
    private EventType eventType; // 이벤트 타입


    /*   생성자   */
    public GameEvent(RBUser user, Game game, EventType eventType) {
        this.user = user;
        this.game = game;
        this.eventTime = LocalDateTime.now();
        this.eventType = eventType;
    }

    // RBUser 엔티티와의 관계를 설정하는 메서드
    public void setUser(RBUser user) {
        this.user = user;
    }
}
