package com.labmate.riddlebox.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_record_id")
    private Long id;  //게임기록 인조식별자

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;  //회원번호

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;  //게임번호

    private int score;  //점수
    private int playTime;  //플레이타임(초?? 이 기준은 이후에)
    private float successRate;  //정답률
    private String result;  //승패



}
