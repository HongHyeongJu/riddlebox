package com.labmate.riddlebox.dto.kakaopay;


import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amount {

    private Integer total;
    private Integer taxFree;
    private Integer vat;
    private Integer point;
    private Integer discount;
    private Integer greenDeposit;
    private Integer taxFreeAmount; // 비과세 금액
    private Integer vatAmount; // 부가세 금액


}
