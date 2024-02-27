package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.RBUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<RBUser, Long> {

    Optional<RBUser> findByLoginEmail(String loginEmail);

}
