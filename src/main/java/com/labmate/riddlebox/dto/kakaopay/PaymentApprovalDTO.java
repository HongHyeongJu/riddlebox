package com.labmate.riddlebox.dto.kakaopay;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentApprovalDTO {

    private String cid; // 가맹점 코드
    private String cid_secret; // 가맹점 코드 인증키
    private String tid; // 결제 고유번호
    private String partner_order_id; // 가맹점 주문번호
    private String partner_user_id; // 가맹점 회원 id
    private String pg_token; // 결제승인 요청을 인증하는 토큰
    private String payload; // 결제 승인 요청에 대해 저장하고 싶은 값
    private Integer total_amount; // 상품 총액

    // 필수 필드를 포함한 생성자
    public PaymentApprovalDTO(String cid, String tid, String partner_order_id, String partner_user_id, String pg_token) {
        this.cid = cid;
        this.tid = tid;
        this.partner_order_id = partner_order_id;
        this.partner_user_id = partner_user_id;
        this.pg_token = pg_token;
    }
}