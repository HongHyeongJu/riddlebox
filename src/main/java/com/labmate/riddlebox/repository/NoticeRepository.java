package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.Faq;
import com.labmate.riddlebox.entity.Notice;
import com.labmate.riddlebox.enumpackage.NoticeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findByStatus(NoticeStatus status, Pageable pageable);
}
