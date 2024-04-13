package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {
    private Long commentId;
    private Long userId;
    private String nickName;
    private String content;
    private Long parentId;
    private int depth;
    private String createdDate;
    private int likesCount;

    //원댓 생성
    public CommentDto(Long commentId, Long userId, String nickName, String content) {
        this.commentId = commentId;
        this.nickName = nickName;
        this.content = content;
        this.parentId = null;
        this.depth = 0;
        this.createdDate = String.valueOf(LocalDateTime.now());
        this.likesCount = 0;
    }


    //좋아요 수 변동 메서드 / 대댓글 메서드
    public void chaneLikeCount(int likeInt) {
        if (!(this.likesCount == 0 && likeInt < 0)) {
            this.likesCount += likeInt;
        }
    }

    // 대댓글 생성 메서드
    public static CommentDto createReply(Long parentId, String nickName, String content, int depth) {
        CommentDto reply = new CommentDto();
        reply.parentId = parentId;
        reply.nickName = nickName;
        reply.content = content;
        reply.depth = depth;
        reply.createdDate = String.valueOf(LocalDateTime.now());
        reply.likesCount = 0;
        return reply;
    }


}
