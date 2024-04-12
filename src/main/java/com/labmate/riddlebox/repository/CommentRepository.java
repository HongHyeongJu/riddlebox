package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.Comment;
import com.labmate.riddlebox.enumpackage.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByGameIdAndStatusOrderByCreatedDateDesc(Long gameId, GameStatus status);

}
