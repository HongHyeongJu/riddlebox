package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.RBRole;
import com.labmate.riddlebox.entity.SocialProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialProfileRepository  extends JpaRepository<SocialProfile, Long> {
}
