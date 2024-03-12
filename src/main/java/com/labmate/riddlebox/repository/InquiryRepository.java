package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.Faq;
import com.labmate.riddlebox.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

}
