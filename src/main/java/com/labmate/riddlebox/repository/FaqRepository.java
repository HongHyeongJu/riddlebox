package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.Faq;
import com.labmate.riddlebox.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Long> {

}
