package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.GameStatus;
import com.labmate.riddlebox.enumpackage.GameSubject;
import com.labmate.riddlebox.enumpackage.NoticeStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;  //카테고리 번호

    private Long parentCategoryId;  //상위 카테고리

    @Enumerated(EnumType.STRING)
    private GameSubject subject;  //주제

    private String description;  //설명
    private int displayOrder;  //게시 순서

    @Enumerated(EnumType.STRING)
    private GameStatus status;  //상태

    private int icon;  //아이콘

    @OneToMany(mappedBy = "gameCategory")
    private List<Game> games = new ArrayList<>();


    /*   생성자   */
    public GameCategory(Long parentCategoryId, GameSubject subject, String description, int displayOrder, int icon) {
        this.parentCategoryId = parentCategoryId;
        this.subject = subject;
        this.description = description;
        this.displayOrder = displayOrder;
        this.icon = icon;
        this.status = GameStatus.ACTIVE; // 초기 상태 설정
    }

    /*    변경 메서드    */
    //카테고리 상태 변경하기
    public void changeStatus(GameStatus newStatus) {
        this.status = newStatus;
    }

    //카테고리 내용 수정 (Game에도 영향)
    public void updateGameCategory(Long newParentCategoryId, GameSubject newSubject, String newDescription) {
        this.parentCategoryId = newParentCategoryId;
        this.subject = newSubject;
        this.description = newDescription;
    }

    //게시순서 변경하기
    public void changeDisplayOrder(int newDisplayOrder) {
        this.displayOrder = newDisplayOrder;
    }


    //아이콘 변경하기
    public void changeIcon(int newIcon) {
        this.icon = newIcon;
    }

    //삭제
    public void softDelete() {
        changeStatus(GameStatus.DELETED);
    }




}




