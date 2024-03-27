package com.labmate.riddlebox.api;

import com.labmate.riddlebox.dto.GameExitRequest;
import com.labmate.riddlebox.dto.PurchaseInformation;
import com.labmate.riddlebox.security.PrincipalDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/point")
public class ApiPointController {

    @PostMapping("patment-page")
    public ResponseEntity<?> buyingPoints(@RequestBody PurchaseInformation purchaseInformation){



        //결제 기록

        int newPoint = 0;
        //결과 페이지로 리디렉션
        String redirectUrl = String.format("/point/payment-completed?newPoint=%d", newPoint);

        return ResponseEntity.ok(Collections.singletonMap("redirectUrl", redirectUrl));
    }


}


