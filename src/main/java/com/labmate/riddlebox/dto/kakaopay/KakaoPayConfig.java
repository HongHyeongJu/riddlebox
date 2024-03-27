package com.labmate.riddlebox.dto.kakaopay;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoPayConfig {

    @Value("${kakaopay.host}")
    private String host;

    @Value("${kakaopay.request-uri}")
    private String requestUri;

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



}
