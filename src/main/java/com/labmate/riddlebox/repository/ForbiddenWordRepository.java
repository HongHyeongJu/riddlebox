package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.ForbiddenWord;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ForbiddenWordRepository extends JpaRepository<ForbiddenWord, Long> {

    boolean existsByWord(String nickname);
}
