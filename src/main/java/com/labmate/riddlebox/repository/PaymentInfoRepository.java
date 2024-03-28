package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.GameRecord;
import com.labmate.riddlebox.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
}
