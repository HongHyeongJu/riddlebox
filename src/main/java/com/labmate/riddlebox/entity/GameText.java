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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", unique = true)
    // Game과 1:1 관계 설정, unique = true로 설정하여 Game과 GameText 간에 1:1 관계가 유일함을 보장
    private Game game;  //게임 번호

    @Lob  // 긴 텍스트를 위한 애너테이션
    @Column(columnDefinition = "MEDIUMTEXT")
    private String text;  //내용

    @Enumerated(EnumType.STRING)
    private GameStatus status;  //상태


    public GameText(Long id, Game game, String text, GameStatus status) {
        this.id = id;
        this.game = game;
        this.text = text;
        this.status = status;
    }

    public void setGame(Game game) {
        this.game = game;

        // 새로운 Game 엔티티와의 관계를 설정합니다.
        if (game != null && game.getGameText() != this) {
            game.setGameText(this);
        }
    }

    public void updateText(String newText) {
        this.text = newText;
    }


}
