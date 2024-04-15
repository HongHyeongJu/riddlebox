package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.dto.CommentDto;
import com.labmate.riddlebox.enumpackage.GameStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    // 문의자 - 사용자 역할
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RBUser user;

    // 문의자 - 사용자 역할
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentId;

    @Enumerated(EnumType.STRING)
    private GameStatus status;  //상태
    @Column(name = "is_active", insertable = false, updatable = false)
    private Boolean isActive; //부분 인덱스

    @Column(nullable = false)
    private int depth;

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column
    private LocalDateTime modifiedDate;

    public void setUser(RBUser user) {
        this.user = user;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Comment(String content) {
        this.content = content;
        this.parentId = null;
        this.depth = 0;
        this.status = GameStatus.ACTIVE;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    public static Comment createReply(String content, Comment parentId, int depth) {
        Comment reply = new Comment(content);
        reply.parentId = parentId;
        reply.depth = depth;
        return reply;
    }

    public void deleteComment() {
        this.status = GameStatus.DELETED;
        this.modifiedDate = LocalDateTime.now();
    }

    public void modifyComment(String newContent) {
        this.content = newContent;
        this.modifiedDate = LocalDateTime.now();
    }


}
