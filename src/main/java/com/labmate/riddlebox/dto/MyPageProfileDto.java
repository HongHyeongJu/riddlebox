package com.labmate.riddlebox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MyPageProfileDto {
    private Long userId;
    private String loginEmail;
    private String name;
    private String nickname;
    private LocalDateTime regDate;

    public MyPageProfileDto(Long userId, String loginEmail, String name, String nickname, LocalDateTime regDate) {
        this.userId = userId;
        this.loginEmail = loginEmail;
        this.name = name;
        this.nickname = nickname;
        this.regDate = regDate;
    }

}
