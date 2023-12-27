package com.labmate.riddlebox.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})  //"team"쓰면 무한루프 빠진다 조심
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) //모든 연관관계 지연로딩 하기 !!
    @JoinColumn(name = "team_id")
    private Team team;

//    protected Member(){}

    public Member(String username){
        this(username,0,null);
    }
    public Member(String username, int age){
        this(username,age,null);
    }

    public Member(String username, int age, Team team) {
        this.username=username;
        this.age=age;
        if(team!=null){
            this.team=team;
        }
    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }

}