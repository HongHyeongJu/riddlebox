package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameCategoryRepository  extends JpaRepository<GameCategory, Long> {
}
