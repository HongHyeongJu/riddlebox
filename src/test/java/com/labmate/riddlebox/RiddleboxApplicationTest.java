package com.labmate.riddlebox;

import com.labmate.riddlebox.entity.Hello;
import com.labmate.riddlebox.entity.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@SpringBootTest
class RiddleboxApplicationTest {

//    @Autowired
    @PersistenceContext
EntityManager em;

    @Test
    void contextLoads() {
        //엔티티 저장
        Hello hello = new Hello();
        em.persist(hello);

        //쿼리 날리기
        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qHello = QHello.hello;

        Hello result =  query
                        .selectFrom(qHello)
                        .fetchOne();

        //static import 단축키 alt+Enter
        Assertions.assertThat(result).isEqualTo(hello);
        Assertions.assertThat(result.getId()).isEqualTo(hello.getId());



    }

}
