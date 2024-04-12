package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.enumpackage.GameLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreateCommentDto {

    private String content;
    private Long parentId;
    private int depth;

    public CreateCommentDto(String content, Long parentId, int depth) {
        this.content = content;
        this.parentId = parentId;
        this.depth = depth;
    }

    public CreateCommentDto(String content) {
        this.content = content;
        this.parentId = null;
        this.depth = 0;
    }

}
