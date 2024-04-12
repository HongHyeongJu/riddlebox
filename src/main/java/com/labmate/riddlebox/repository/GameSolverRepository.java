package com.labmate.riddlebox.repository;


import com.labmate.riddlebox.entity.GameSolver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameSolverRepository extends JpaRepository<GameSolver, Long> {
    Optional<GameSolver> findByGame_Id(Long gameId);
}
