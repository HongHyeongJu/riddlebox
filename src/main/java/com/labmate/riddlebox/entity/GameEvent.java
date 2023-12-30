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
    @JoinColumn(name = "member_id")
    private Member member;  //회원번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;  //게임번호

    private LocalDateTime eventTime;

    @Enumerated(EnumType.STRING)
    private EventType eventType; // 이벤트 타입


    /*   생성자   */
    public GameEvent(Member member, Game game, EventType eventType) {
        this.member = member;
        this.game = game;
        this.eventTime = LocalDateTime.now();
        this.eventType = eventType;
    }
}
