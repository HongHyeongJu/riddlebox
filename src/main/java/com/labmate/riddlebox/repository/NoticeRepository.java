package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.Faq;
import com.labmate.riddlebox.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
