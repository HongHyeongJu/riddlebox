package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.enumpackage.GameSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameCategoryRepository  extends JpaRepository<GameCategory, Long> {
    Optional<GameCategory> findBySubject(GameSubject gameSubject);
}
