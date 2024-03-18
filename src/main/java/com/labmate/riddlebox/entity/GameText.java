package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.GameStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameText extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_text_id")
    private Long id;  //인조식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;  //게임번호

    private String gameText;  //문제의 순서를 제어

    @Enumerated(EnumType.STRING)
    private GameStatus status;  //상태




    /*    생성 메서드    */
    public void setGame(Game game) {
        if (this.game != null) {
            this.game.getGameContents().remove(this);
        }
        this.game = game;
    }


    /*    변경 메서드    */
    //게임 데이터 상태 변경하기
    public void changeStatus(GameStatus newStatus) {
        this.status = newStatus;
    }


    //게임 데이터 수정 (Game에도 영향)
    public void updateGameContent(String newQuestion, String newAnswer, Game newGame) {

        //이전 카테고리에서 게임 데이터 제거
        if (this.game != null) {
            this.game.getGameContents().remove(this);
        }

        this.question = newQuestion;
        this.answer = newAnswer;
        this.game = newGame;

        //새로운 데이터 추가
        newGame.getGameContents().add(this);
    }

    //문제 List만 전달하기
    public String getQuestion(){
        return question;
    }



    //삭제
    public void softDelete() {
        changeStatus(GameStatus.DELETED);
    }

    //문제 출제순서 변경
    public void setOrdering(int newOrdering) {
        this.ordering = newOrdering;
    }


}
