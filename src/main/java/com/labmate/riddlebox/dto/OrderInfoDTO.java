package com.labmate.riddlebox.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class OrderInfoDTO {

    private Integer point_100_Quantity;
    private Integer point_1000_Quantity;
    private String paymentMethod; // 결제 방법
    private String paymentId; // 결제ID

    public OrderInfoDTO(Integer point_100_Quantity, Integer point_1000_Quantity, String paymentMethod, String paymentId) {
        this.point_100_Quantity = point_100_Quantity;
        this.point_1000_Quantity = point_1000_Quantity;
        this.paymentMethod = paymentMethod;
        this.paymentId = paymentId;
    }
}
