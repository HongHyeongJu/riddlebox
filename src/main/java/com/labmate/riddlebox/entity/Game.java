package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.GameLevel;
import com.labmate.riddlebox.enumpackage.GameStatus;
import com.labmate.riddlebox.enumpackage.GameSubject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String description;  //설명

    @Enumerated(EnumType.STRING)
    private GameLevel gameLevel;  //게임 난이도

    @Enumerated(EnumType.STRING)
    private GameStatus status;  //상태

    private int viewCount;  //조회수

    private String author;  //작가
    private LocalDateTime officialReleaseDate;  //정식 공개일
    private LocalDateTime officialUpdateDate;  //공식 업데이트일

    @OneToMany(mappedBy = "game")
    private List<GameRecord> gameRecords = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    private List<GameContent> gameContents = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    private List<GameImage> gameImages = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    private List<RecommendGame> recommendGames = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    private List<GameEvent> gameEvents = new ArrayList<>();


    /*   생성자   */
    public Game(GameCategory gameCategory, String title, String description, GameLevel gameLevel, String author) {
        this.gameCategory = gameCategory;
        this.title = title;
        this.description = description;
        this.gameLevel = gameLevel;
        this.author = author;
        this.status = GameStatus.ACTIVE; // 초기 상태 설정
        this.officialReleaseDate = LocalDateTime.now(); // 생성일 설정
        this.officialUpdateDate = LocalDateTime.now(); // 수정일 설정
        this.viewCount = 0; // 조회수 초기화
    }


    /*    생성 메서드    */
    public void addGameContent(GameContent gameContent) {
        gameContents.add(gameContent);
        gameContent.setGame(this);
    }

    public void addGameImage(GameImage gameImage) {
        gameImages.add(gameImage);
        gameImage.setGame(this);
    }


    /*    변경 메서드    */
    //게임 상태 변경하기
    public void changeStatus(GameStatus newStatus) {
        this.status = newStatus;
    }

    //게임 내용 수정 (GameCategory 에도 영향)
    public void updateGame(String newTitle, String newDescription,
                           GameLevel newGameLevel, String newAuthor,
                           GameCategory newGameCategory) {

        if (newTitle == null || newTitle.isEmpty() || newDescription == null || newDescription.isEmpty()) {
            throw new IllegalArgumentException("제목과 설명을 모두 작성해주세요");
        }

        // 이전 카테고리에서 게임 제거
        if (this.gameCategory != null) {
            this.gameCategory.getGames().remove(this);
        }

        this.title = newTitle;
        this.description = newDescription;
        this.gameLevel = newGameLevel;
        this.author = newAuthor;
        this.gameCategory = newGameCategory;
        this.officialUpdateDate = LocalDateTime.now();
        newGameCategory.getGames().add(this);
    }

    //게임 생성일, 공개일 수정
    public void updateGameofficialDate(LocalDateTime newOfficialReleaseDate, LocalDateTime newOfficialUpdateDate){
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
