package com.labmate.riddlebox.dto.kakaopay;

import com.labmate.riddlebox.dto.GameplayInfoDto;
import com.labmate.riddlebox.entity.Game;
import com.labmate.riddlebox.entity.PaymentInfo;
import com.labmate.riddlebox.enumpackage.PaymentStatus;
import com.labmate.riddlebox.repository.GameRecordRepository;
import com.labmate.riddlebox.repository.GameRepository;
import com.labmate.riddlebox.repository.PaymentInfoRepository;
import com.labmate.riddlebox.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.labmate.riddlebox.entity.QPaymentInfo.paymentInfo;

@Service
public class KakaoPayService {

    private final RestTemplate restTemplate;
    private final KakaoPayConfig kakaoPayConfig;
    private final JPAQueryFactory queryFactory;
    private final UserRepository userRepository;
    private final PaymentInfoRepository paymentInfoRepository;

    @Autowired
    public KakaoPayService(EntityManager em, UserRepository userRepository,
                           PaymentInfoRepository paymentInfoRepository,
                           RestTemplate restTemplate, KakaoPayConfig kakaoPayConfig) {
        this.queryFactory = new JPAQueryFactory(em);
        this.userRepository = userRepository;
        this.paymentInfoRepository = paymentInfoRepository;
        this.restTemplate = restTemplate;
        this.kakaoPayConfig = kakaoPayConfig;
    }


    @Transactional
    public PaymentInfo findAwaitingPaymentPaymentInfo(Long gameId) {
        PaymentInfo fidPaymentInfo = queryFactory.selectFrom(paymentInfo)
                                                    .where(paymentInfo.user.id.eq(gameId),
                                                            paymentInfo.status.eq(PaymentStatus.AWAITING_PAYMENT))
                                                    .fetchOne();
        return fidPaymentInfo;
    }


    public String requestPaymentReady(PaymentRequestDTO paymentRequestDTO) throws Exception {
        String url = "https://" + kakaoPayConfig.getHost() + kakaoPayConfig.getRequestUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", kakaoPayConfig.getClientSecret()); // Admin Key

        // PaymentRequestDTO를 JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(paymentRequestDTO);

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // 응답 처리 및 redirect_url 반환
        return response.getBody(); // 예제에서는 단순화를 위해 전체 응답 바디를 반환
    }


}
