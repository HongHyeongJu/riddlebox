package com.labmate.riddlebox.entity;

import com.labmate.riddlebox.dto.kakaopay.Amount;
import com.labmate.riddlebox.dto.kakaopay.CardInfo;
import com.labmate.riddlebox.enumpackage.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "payment_info")
public class PaymentInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_info_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RBUser user; // 사용자 ID

    private String aid; // 요청 고유 번호 - 승인/취소가 구분된 결제번호
    private String tid; // 결제 고유 번호 - 승인/취소가 동일한 결제번호
    private String partnerOrderId; // 가맹점 주문번호
    private String partnerUserId; // 가맹점 회원 ID

    @Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
    private PaymentStatus status; // 결제 상태

    private String itemName;
    private String itemCode;
    private Integer totalPointAmount; // 고객이 구매하는 총 포인트 금액
    private Integer totalAmount; // 상품 총액, 선택적 사용
    private LocalDateTime createdAt; // 결제 준비 요청 시각
    private LocalDateTime approvedAt; // 결제 승인 시각

    @Embedded
    private Amount amount;
    @Embedded
    private CardInfo cardInfo;



    public void setUser(RBUser user) {
        // 이전에 참조하던 관계를 제거
        if (this.user != null) {
            this.user.getGameRecords().remove(this);
        }
        // 새로운 객체 참조 설정
        this.user = user;
    }

    public void firstPaymentInformation(RBUser user,  String partnerOrderId ,String partnerUserId,
                                  String itemName, String itemCode, Integer totalAmount, Integer totalPointAmount){
        this.user = user;
        this.partnerOrderId = partnerOrderId;
        this.partnerUserId = partnerUserId;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.totalAmount = totalAmount;
        this.totalPointAmount = totalPointAmount;

    }

    public void updatePaymentStatus(PaymentStatus status){
        this.status = status;
    }

    public void secondPaymentInformation(String tid, LocalDateTime createdAt){
        this.tid = tid;
        this.createdAt = createdAt;
    }

    public void thirdPaymentInformation(String aid, Amount amount, CardInfo cardInfo, LocalDateTime approvedAt ){
        this.aid = aid;
        this.amount = amount;
        this.cardInfo = cardInfo;
        this.approvedAt = approvedAt;
    }


}
