package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

}
