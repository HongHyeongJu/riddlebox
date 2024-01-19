package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.Admin;
import com.labmate.riddlebox.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Member, Long> {

    Optional<Admin> findByLoginId(String loginId);


}