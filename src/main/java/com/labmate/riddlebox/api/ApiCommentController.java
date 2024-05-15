package com.labmate.riddlebox.api;


import com.labmate.riddlebox.dto.CommentDto;
import com.labmate.riddlebox.dto.CreateCommentDto;
import com.labmate.riddlebox.security.SecurityUtils;
import com.labmate.riddlebox.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class ApiCommentController {


    @Autowired
    private CommentService commentService;

    // 사용자 ID로 내 댓글만 모아보기
    @GetMapping("/mycomments")
    public ResponseEntity<Page<CommentDto>> getCommentsByUserId(@RequestParam(required = false) Long gameId, Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<CommentDto> comments = commentService.getGameCommentsPagingByUserId(gameId, userId, pageable);
        return ResponseEntity.ok(comments);
    }


    // 새 댓글 등록
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CreateCommentDto createCommentDto) {
        Long userId = SecurityUtils.getCurrentUserId();
        CommentDto comment = commentService.createComment(userId, createCommentDto);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    // 댓글 전체 수정 - 내 프로젝트에는 사용 안되는 기능
//    @PutMapping("/{id}")
//    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, @RequestBody CreateCommentDto updateCommentDto) {
//        CommentDto updatedComment = commentService.updateComment(id, updateCommentDto);
//        return ResponseEntity.ok(updatedComment);
//    }

    // 댓글 부분 수정 (내용만 수정)
    @PatchMapping("/{commentid}")
    public ResponseEntity<CommentDto> modifyCommentContent(@PathVariable Long commentid, @RequestBody String newContent) {
        Long userId = SecurityUtils.getCurrentUserId();
        CommentDto updatedComment = commentService.modifyCommentContent(commentid, userId, newContent);
        return ResponseEntity.ok(updatedComment);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentid}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentid) {
        Long userId = SecurityUtils.getCurrentUserId();
        commentService.deleteComment(commentid, userId);
        return ResponseEntity.noContent().build();
    }

}
