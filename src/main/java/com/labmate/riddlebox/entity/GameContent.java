package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "question", "answer", "status"})  //"team"쓰면 무한루프 빠진다 조심
public class GameContent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_content_id")
    private Long id;  //게임기록 인조식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;  //게임번호

    private String question;  //질문
    private String answer;  //정답 복수정답시 구분자-@-  ex) -강아지-개-Dog-  그리고 일반 답변도 앞뒤로 -붙이기-

    @Enumerated(EnumType.STRING)
    private GameStatus status;  //상태

    private Integer ordering;  //문제의 순서를 제어


    /*   생성자   */
    public GameContent(String question, String answer, Integer ordering) {
        this.question = question;
        this.answer = answer;
        this.status = GameStatus.ACTIVE; // 초기 상태 설정
        if (ordering == null) {
            this.ordering = 1;
        } else {
            this.ordering = ordering;
        }
    }

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
    public String getQuestion() {
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
