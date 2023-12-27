package com.labmate.riddlebox;


import com.labmate.riddlebox.entity.Member;
import com.labmate.riddlebox.repository.MemberJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
//@Rollback(false)
public class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;


}
