package com.labmate.riddlebox.enumpackage;

public enum PaymentStatus {
    INITIATED("결제 시작됨"), // 사용자가 결제 시작을 요청했지만, 아직 카카오페이 결제 화면으로 이동하지 않은 상태
    AWAITING_PAYMENT("결제 대기중"), // 사용자가 결제 화면에 있으며, 결제 수단 선택 또는 비밀번호 입력을 기다리는 상태
    AUTHORIZED("결제 인증됨"), // 사용자가 결제 수단을 선택하고 인증(비밀번호 입력 등)을 완료한 상태, 승인 대기중
    APPROVED("결제 승인됨"), // 결제 승인이 완료되어 결제가 성공적으로 이루어진 상태
    CANCELED("결제 취소됨"), // 사용자 또는 가맹점에 의해 결제가 취소된 상태
    FAILED("결제 실패"), // 결제 승인 시도 중 오류가 발생하여 결제가 실패한 상태
    REFUNDED("결제 환불됨"); // 결제 완료 후 환불이 이루어진 상태

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
