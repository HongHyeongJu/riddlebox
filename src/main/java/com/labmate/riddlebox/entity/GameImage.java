package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.enumpackage.GameStatus;
import com.labmate.riddlebox.enumpackage.ImageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_image_id")
    private Long id;  //게임기록 인조식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;  //게임번호

    private String fileOriginName;  //파일원본이름
    private String fileSaveName;  //파일저장이름

    @Enumerated(EnumType.STRING)
    private ImageType fileType;  //파일타입

    private String filePath;  //파일경로
    private Long fileSize;  //파일크기
    private String fileUrl;  //file_url
    private String description;  //설명

    @Enumerated(EnumType.STRING)
    private GameStatus status;  //상태


    /*    생성자    */
    public GameImage(Game game, String fileOriginName, String fileSaveName, ImageType fileType,
                     String filePath, Long fileSize, String fileUrl, String description) {
        this.game = game;
        this.fileOriginName = fileOriginName;
        this.fileSaveName = fileSaveName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.description = description;
        this.status = GameStatus.ACTIVE; // 초기 상태 설정
    }



    /*    생성 메서드    */
    public void setGame(Game game) {
        this.game = game;
    }


    /*    변경 메서드    */
    //게임 이미지 상태 변경하기
    public void changeStatus(GameStatus newStatus) {
        this.status = newStatus;
    }

    //게임 이미지 수정 (Game에도 영향)
    public void updateGameContent(String newFileOriginName, String newFileSaveName,
                                  ImageType newFileType, String newFilePath, Long newFileSize,
                                  String newFileUrl, String newDescription,
                                  Game newGame) {

        //이전 카테고리에서 게임 이미지 제거
        if (this.game != null) {
            this.game.getGameContents().remove(this);
        }

        this.fileOriginName = newFileOriginName;
        this.fileSaveName = newFileSaveName;
        this.fileType = newFileType;
        this.filePath = newFilePath;
        this.fileSize = newFileSize;
        this.fileUrl = newFileUrl;
        this.description = newDescription;

        //새로운 데이터 추가
        newGame.getGameImages().add(this);
    }

    //삭제
    public void softDelete() {
        changeStatus(GameStatus.DELETED);
    }

}




