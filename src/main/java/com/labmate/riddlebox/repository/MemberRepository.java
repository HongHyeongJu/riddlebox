package com.labmate.riddlebox.repository;
import com.labmate.riddlebox.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}