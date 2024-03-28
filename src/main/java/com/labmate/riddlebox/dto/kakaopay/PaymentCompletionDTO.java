package com.labmate.riddlebox.dto.kakaopay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentCompletionDTO {

    private String aid;
    private String tid;
    private String cid;
    private String sid; // 필드 추가
    private String partnerOrderId;
    private String partnerUserId;
    private String paymentMethodType;
    private String itemName;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    private Amount amount; // 금액 정보
    private CardInfo cardInfo; // 카드 정보, 결제 수단이 카드일 때만 존재

}