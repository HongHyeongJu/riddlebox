package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.SocialProfile;
import com.labmate.riddlebox.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
