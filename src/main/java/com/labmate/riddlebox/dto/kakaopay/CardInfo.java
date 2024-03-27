package com.labmate.riddlebox.dto.kakaopay;


import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardInfo {
    private String kakaopayPurchaseCorp;
    private String kakaopayPurchaseCorpCode;
    private String kakaopayIssuerCorp;
    private String kakaopayIssuerCorpCode;
    private String bin;
    private String cardType;
    private String installMonth;
    private String approvedId;
    private String cardMid;
    private String interestFreeInstall;
    private String installmentType;
    private String cardItemCode;
}