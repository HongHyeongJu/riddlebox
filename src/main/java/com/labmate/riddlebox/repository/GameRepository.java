package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.entity.Comment;
import com.labmate.riddlebox.entity.Game;
import com.labmate.riddlebox.enumpackage.GameStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findFirstByGameCategory_IdAndStatusOrderByCreatedDateDesc(Long categoryId, GameStatus status);

}