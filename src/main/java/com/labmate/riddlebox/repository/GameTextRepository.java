package com.labmate.riddlebox.repository;


import com.labmate.riddlebox.entity.GameContent;
import com.labmate.riddlebox.entity.GameText;
import com.labmate.riddlebox.enumpackage.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameTextRepository extends JpaRepository<GameText, Long> {
    Optional<GameText> findByGame_Id(Long gameId);
}
