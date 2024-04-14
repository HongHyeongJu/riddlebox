package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameStatus;
import com.labmate.riddlebox.enumpackage.GameSubject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "title", "description", "gameLevel"})  //"team"쓰면 무한루프 빠진다 조심
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;  //게임 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private GameCategory gameCategory;  //게임 카테고리

    private String title;  //제목

    @Lob  // 긴 텍스트를 위한 애너테이션
    private String descript;  //설명

    @Enumerated(EnumType.STRING)
    private GameLevel gameLevel;  //게임 난이도

    @Enumerated(EnumType.STRING)
    private GameStatus status;  //상태

    private int viewCount;  //조회수

    private String author;  //작가
    private LocalDateTime officialReleaseDate;  //정식 공개일
    private LocalDateTime officialUpdateDate;  //공식 업데이트일

    @OneToOne(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private GameText gameText;  // This field represents the 1:1 association with GameText

    @OneToMany(mappedBy = "game")
    private List<GameRecord> gameRecords = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GameContent> gameContents = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<GameImage> gameImages = new HashSet<>();

    @OneToMany(mappedBy = "game")
    private List<RecommendGame> recommendGames = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    private List<GameEvent> gameEvents = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    private List<Comment> comments = new ArrayList<>();


    @OneToOne(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private GameSolver gameSolver;


    /*   생성자   */
    public Game(GameCategory gameCategory, String title, String descript, GameLevel gameLevel, String author) {
        this.gameCategory = gameCategory;
        this.title = title;
        this.descript = descript;
        this.gameLevel = gameLevel;
        this.status = GameStatus.ACTIVE; // 초기 상태 설정
        this.viewCount = 0; // 조회수 초기화
        this.author = author;
        this.officialReleaseDate = LocalDateTime.now(); // 생성일 설정
        this.officialUpdateDate = LocalDateTime.now(); // 수정일 설정
    }

    /*    헬프 메서드    */
    public void addGameRecord(GameRecord gameRecord) {
        this.gameRecords.add(gameRecord);
        gameRecord.setGame(this);
    }

    public void removeGameRecord(GameRecord gameRecord) {
        this.gameRecords.remove(gameRecord);
        gameRecord.setGame(null);
    }

    public void addGameContent(GameContent gameContent) {
        this.gameContents.add(gameContent);
        gameContent.setGame(this);
    }

    public void removeGameContent(GameContent gameContent) {
        this.gameContents.remove(gameContent);
        gameContent.setGame(null);
    }

    public void addGameImage(GameImage gameImage) {
        this.gameImages.add(gameImage);
        gameImage.setGame(this);
    }

    public void removeGameImage(GameImage gameImage) {
        this.gameImages.remove(gameImage);
        gameImage.setGame(null);
    }

    public void addRecommendGame(RecommendGame recommendGame) {
        this.recommendGames.add(recommendGame);
        recommendGame.setGame(this);
    }

    public void removeRecommendGame(RecommendGame recommendGame) {
        this.recommendGames.remove(recommendGame);
        recommendGame.setGame(null);
    }

    public void addGameEvent(GameEvent gameEvent) {
        this.gameEvents.add(gameEvent);
        gameEvent.setGame(this);
    }

    public void removeGameEvent(GameEvent gameEvent) {
        this.gameEvents.remove(gameEvent);
        gameEvent.setGame(null);
    }


    // 댓글 추가
    public void addComment(Comment comment) {
        this.comments.add(comment);
        if (comment.getGame() != this) {
            comment.setGame(this);
        }
    }

    // 댓글 제거
    public void removeComment(Comment comment) {
        this.comments.remove(comment);
        if (comment.getGame() == this) {
            comment.setGame(null);
        }
    }

    public void setGameSolver(GameSolver gameSolver) {
        // 현재 게임 솔버가 이미 설정되어 있는지 검사
        if (this.gameSolver != null) {
            // 기존 게임 솔버가 있다면, 연관 관계를 제거
            this.gameSolver.setGame(null);
        }

        // 새로운 게임 솔버 설정
        this.gameSolver = gameSolver;

        if (gameSolver != null && gameSolver.getGame() != this) {
            // GameSolver 내부에서 Game을 설정하지 않습니다.
            // 이 방법으로 무한 루프를 방지합니다.
            gameSolver.setGame(this);
        }
    }


    public void setGameText(GameText gameText) {
        // 현재 게임 솔버가 이미 설정되어 있는지 검사
        if (this.gameText != null) {
            // 기존 게임 솔버가 있다면, 연관 관계를 제거
            this.gameText.setGame(null);
        }

        // 새로운 게임 솔버 설정
        this.gameText = gameText;

        if (gameText != null && gameText.getGame() != this) {
            // GameSolver 내부에서 Game을 설정하지 않습니다.
            // 이 방법으로 무한 루프를 방지합니다.
            gameText.setGame(this);
        }
    }

    public void setGameCategory(GameCategory gameCategory) {
        if (gameCategory != null) {
            gameCategory.getGames().add(this);
        }
        this.gameCategory = gameCategory;
    }



    /*    변경 메서드    */
    //게임 상태 변경하기
    public void changeStatus(GameStatus newStatus) {
        this.status = newStatus;
    }

    //게임 내용 수정 (GameCategory 에도 영향)
    @PreAuthorize("hasRole('TEAM_LEADER')")   //메서드 레벨 보안 적용
    public void updateGame(String newTitle, String newDescript,
                           GameLevel newGameLevel, String newAuthor,
                           GameCategory newGameCategory) {

        if (newTitle == null || newTitle.isEmpty() || newDescript == null || newDescript.isEmpty()) {
            throw new IllegalArgumentException("제목과 설명을 모두 작성해주세요");
        }

        // 이전 카테고리에서 게임 제거
        if (this.gameCategory != null) {
            this.gameCategory.getGames().remove(this);
        }

        this.title = newTitle;
        this.descript = newDescript;
        this.gameLevel = newGameLevel;
        this.author = newAuthor;
        this.gameCategory = newGameCategory;
        this.officialUpdateDate = LocalDateTime.now();
        newGameCategory.getGames().add(this);
    }

    //게임 생성일, 공개일 수정
    public void updateGameofficialDate(LocalDateTime newOfficialReleaseDate, LocalDateTime newOfficialUpdateDate) {
        this.officialReleaseDate = newOfficialReleaseDate;
        this.officialUpdateDate = newOfficialUpdateDate;
    }


    //삭제
    public void softDelete() {
        changeStatus(GameStatus.DELETED);
    }

    //조회수 증가
    public void addViewCount() {
        this.viewCount++;
    }


}
