package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.entity.RBRole;
import com.labmate.riddlebox.entity.RBUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RBRole, Long> {

    Optional<RBRole> findByName(String user);
}
