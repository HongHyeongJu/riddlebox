package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.GameContent;
import com.labmate.riddlebox.entity.GameImage;
import com.labmate.riddlebox.enumpackage.GameStatus;
import com.labmate.riddlebox.enumpackage.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.Optional;

public interface GameImageRepository extends JpaRepository<GameImage, Long> {
    Optional<GameImage> findByGame_IdAndImageType(Long gameId, ImageType imageType);
}
