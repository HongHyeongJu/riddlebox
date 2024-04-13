package com.labmate.riddlebox.controller;


import com.labmate.riddlebox.dto.CommentDto;
import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.service.TimeLimitGameService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class ApiCommentController {

    private final TimeLimitGameService timeLimitGameService;

    public ApiCommentController(TimeLimitGameService timeLimitGameService) {
        this.timeLimitGameService = timeLimitGameService;
    }


    @GetMapping
    public ResponseEntity<Page<CommentDto>> getCommentsByGameId(@PathVariable Long gameId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<CommentDto> comments = timeLimitGameService.getOtherCommentsByGameId(gameId, pageable);
        return ResponseEntity.ok(comments);
    }


}
