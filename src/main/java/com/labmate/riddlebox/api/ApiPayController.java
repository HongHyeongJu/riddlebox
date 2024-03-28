package com.labmate.riddlebox.api;

import com.labmate.riddlebox.dto.PurchaseInformation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/pay")
public class ApiPayController {

    //todo 다양한 결제서비스에 공통적인 로직을 추출하는 리팩토링 추후에 할 것
    @PostMapping("point-payment")
    public ResponseEntity<?> kakaoPayRequest_01(@RequestBody PurchaseInformation purchaseInformation){
        //받은 정보로 카카오 결제 요청 보내기 (+admin-key)


        // 응답받은 next_redirect_pc_url 사용자에게 제공

        //결제 기록

        int newPoint = 0;
        //결과 페이지로 리디렉션
        String next_redirect_pc_url = String.format("/point/payment-completed?newPoint=%d", newPoint);

        return ResponseEntity.ok(Collections.singletonMap("next_redirect_pc_url", next_redirect_pc_url));
    }


//    @PostMapping("kakaopay")
    public ResponseEntity<?> kakaoPayRequest_02(@RequestBody PurchaseInformation purchaseInformation){
        //결제 승인 받으면 해당 값 만큼 포인트 적립, 포인트 결제 성공 페이지로 redirect


        // 응답받은 next_redirect_pc_url 사용자에게 제공

        //결제 기록

        int newPoint = 0;
        //결과 페이지로 리디렉션
        String next_redirect_pc_url = String.format("/point/payment-completed?newPoint=%d", newPoint);

        return ResponseEntity.ok(Collections.singletonMap("next_redirect_pc_url", next_redirect_pc_url));
    }


}


