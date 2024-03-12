package com.labmate.riddlebox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyPageDto {
    private Long userId;
    private String nickname;
    private int point=0;
    private String currentRecord;
    private String profileImg;


    public MyPageDto(Long userId, String nickname, int point, String currentRecord, String profileImg) {
        this.userId = userId;
        this.nickname = nickname;
        this.point = point;
        this.currentRecord = currentRecord;
        this.profileImg = profileImg;
    }

}
