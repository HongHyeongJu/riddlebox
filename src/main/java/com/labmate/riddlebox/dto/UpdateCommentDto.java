package com.labmate.riddlebox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCommentDto {

    private Long commentId;
    private String content;

    public UpdateCommentDto(Long commentId, String content) {
        this.commentId = commentId;
        this.content = content;
    }
}
