package com.labmate.riddlebox.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;  //카테고리 번호

    private Long parentCategoryId;  //상위 카테고리
    private String subject;  //주제
    private String description;  //설명
    private int displayOrder;  //게시 순서
    private String status;  //상태
    private int icon;  //아이콘


    @OneToMany(mappedBy = "game")
    private List<Game> games = new ArrayList<>();

}




