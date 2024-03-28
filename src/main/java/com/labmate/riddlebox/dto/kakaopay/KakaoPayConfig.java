package com.labmate.riddlebox.dto.kakaopay;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoPayConfig {

    @Value("${kakaopay.host}")
    private String host;

    @Value("${kakaopay.ready-request-uri}")
    private String readyRequestUri;

    @Value("${kakaopay.approve-request-uri}")
    private String approveRequestUri;

    @Value("${kakaopay.secret-key}")
    private String secretKey;

    @Value("${kakaopay.secret-key-dev}")
    private String secretKeyDev;

    @Value("${kakaopay.cid}")
    private String cid;

    @Value("${kakaopay.client-id}")
    private String clitentId;

    @Value("${kakaopay.client-secret}")
    private String clientSecret;

    private String readyRequestUriCompletion = "https://open-api.kakaopay.com/online/v1/payment/ready";

    private String approveRequestUriCompletion = "https://open-api.kakaopay.com/online/v1/payment/approve";


}
