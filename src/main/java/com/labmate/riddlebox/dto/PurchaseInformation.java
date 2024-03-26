package com.labmate.riddlebox.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class PurchaseInformation {

    private int point_100_Quantity;
    private int point_1000_Quantity;
    private String paymentMethod; // 결제 방법

}
