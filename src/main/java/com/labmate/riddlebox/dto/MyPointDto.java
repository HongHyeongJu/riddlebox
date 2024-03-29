package com.labmate.riddlebox.dto;

import com.labmate.riddlebox.entity.GameCategory;
import com.labmate.riddlebox.enumpackage.GameResultType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MyPointDto {

    private Long userPointId;
    private String reason;
    private int earnedPoints;  //적립포인트
    private LocalDateTime earnedDate;  //적립일
    private int totalPoints;  //누적포인트

    public MyPointDto(Long userPointId, String reason, int earnedPoints, LocalDateTime earnedDate, int totalPoints) {
        this.userPointId = userPointId;
        this.reason = reason;
        this.earnedPoints = earnedPoints;
        this.earnedDate = earnedDate;
        this.totalPoints = totalPoints;
    }

}
