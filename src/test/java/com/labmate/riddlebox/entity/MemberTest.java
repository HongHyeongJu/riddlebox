package com.labmate.riddlebox.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.labmate.riddlebox.entity.QMember.member;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1" , 10, teamA);
        Member member2 = new Member("member2" , 20, teamA);

        Member member3 = new Member("member3" , 30, teamB);
        Member member4 = new Member("member4" , 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();

        //확인
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        for (Member member : members) {
            System.out.println("member= "+member);
            System.out.println("-> member.team= "+member.getTeam());
        }

    }

    @Test
    public void startJPQL(){
        //member1 찾기
        String queryString = "select m from Member m "+
                             "where m.username = :username";
        Member findMember = em.createQuery(queryString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        //import org.assertj.core.api.Assertions;
        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    @Test
    public void startQuerydsl(){  //Querydsl은 결국 JPQL의 빌더 역할

//        QMember m = new QMember("m"); 1단계
//        QMember m = QMember.member;   2단계


//        Member findMember = queryFactory
//                                .select(QMember.member) 3단계 -> 그런데 이것도 static import할 수 있다

        Member findMember = queryFactory
                                .select(member)
                                .from(member)
                                .where(member.username.eq("member1")) //파라미터바인딩 처리
                                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search(){
        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");


    }









}