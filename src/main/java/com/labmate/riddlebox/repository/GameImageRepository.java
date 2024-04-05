package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.GameImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameImageRepository extends JpaRepository<GameImage, Long> {
}
