package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.UserPoint;
import com.labmate.riddlebox.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {
}
