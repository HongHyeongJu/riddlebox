package com.labmate.riddlebox.repository;


import com.labmate.riddlebox.entity.GameContent;
import com.labmate.riddlebox.enumpackage.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameContentRepository extends JpaRepository<GameContent, Long> {
    Optional<GameContent> findByIdAndStatus(Long id, GameStatus status);

    Optional<GameContent> findByGame_IdAndStatus(Long gameId, GameStatus status);
}
