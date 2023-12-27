package com.labmate.riddlebox.entity;

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
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;  //게임 번호

    @ManyToOne
    @JoinColumn(name = "category_id")
    private GameCategory gameCategory;  //회원번호

    private String title;  //제목
    private String description;  //설명
    private String status;  //상태
    private int view_count;  //조회수
    private String author;  //작가
    private LocalDateTime createDate;  //작가
    private LocalDateTime updateDate;  //작가


    @OneToMany(mappedBy = "game")
    private List<GameRecord> gameRecords = new ArrayList<>();



}
