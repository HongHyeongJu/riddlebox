package com.labmate.riddlebox.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본 생성자를 제공하며, 이 생성자의 접근 수준을 protected로 설정하는 것을 의미(JPA 스펙에 따르면, 모든 엔티티는 기본 생성자를 가지고 있어야함)
//@ToString(of = {"id", "username", "age"})  //"team"쓰면 무한루프 빠진다 조심
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;  //회원번호

    private String username;  //이름
    private String loginId;  //로그인 ID
    private String password;  //비밀번호
    private String email;  //이메일주소
    private String role;  //권한이름
    private String nickname;  //닉네임
    private String snsProvider;  //sns종류
    private String snsId;  //소셜 ID
    private LocalDateTime regDate;  //가입일
    private LocalDateTime lastLoginDate;  //마지막 방문일
    private String status;  //계정 상태


    @OneToMany(mappedBy = "member")
    private List<GameRecord> gameRecords = new ArrayList<>();

//    @ManyToOne(fetch = FetchType.LAZY) //모든 연관관계 지연로딩 하기 !!
//    @JoinColumn(name = "team_id")







}