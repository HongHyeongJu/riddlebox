package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameContent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_content_id")
    private Long id;  //게임기록 인조식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;  //게임번호

    private String question;  //질문
    private String answer;  //정답 복수정답시 구분자 @@

    @Enumerated(EnumType.STRING)
    private GameStatus status;  //상태



    /*   생성자   */
    public GameContent(Game game, String question, String answer) {
        this.game = game;
        this.question = question;
        this.answer = answer;
        this.status = GameStatus.ACTIVE; // 초기 상태 설정
    }

    /*    생성 메서드    */
    public void setGame(Game game) {
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


    //삭제
    public void softDelete() {
        changeStatus(GameStatus.DELETED);
    }


}
