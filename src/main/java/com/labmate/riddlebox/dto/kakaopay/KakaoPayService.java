package com.labmate.riddlebox.dto.kakaopay;

import com.labmate.riddlebox.entity.PaymentInfo;
import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.entity.UserPoint;
import com.labmate.riddlebox.enumpackage.PaymentStatus;
import com.labmate.riddlebox.repository.*;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.labmate.riddlebox.entity.QPaymentInfo.paymentInfo;
import static com.labmate.riddlebox.entity.QUserPoint.userPoint;

@Service
public class KakaoPayService {

    private final RestTemplate restTemplate;
    private final KakaoPayConfig kakaoPayConfig;
    private final JPAQueryFactory queryFactory;
    private final UserRepository userRepository;
    private final PaymentInfoRepository paymentInfoRepository;
    private final UserPointRepository userPointRepository;

    @Autowired
    public KakaoPayService(EntityManager em, UserRepository userRepository,
                           PaymentInfoRepository paymentInfoRepository,
                           RestTemplate restTemplate, KakaoPayConfig kakaoPayConfig, UserPointRepository userPointRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.userRepository = userRepository;
        this.paymentInfoRepository = paymentInfoRepository;
        this.restTemplate = restTemplate;
        this.kakaoPayConfig = kakaoPayConfig;
        this.userPointRepository = userPointRepository;
    }


    @Transactional(readOnly = true)
    public PaymentInfo findAwaitingPaymentPaymentInfo(Long userId) {
        // 현재 시간으로부터 5분 이전 시간 계산
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minus(5, ChronoUnit.MINUTES);

        PaymentInfo fidPaymentInfo = queryFactory.selectFrom(paymentInfo)
                .where(paymentInfo.user.id.eq(userId),
                        paymentInfo.status.eq(PaymentStatus.AWAITING_PAYMENT), //결제 대기
                        paymentInfo.createdDate.after(fiveMinutesAgo)) // 5분 이내 조건 추가
                .orderBy(paymentInfo.createdDate.desc()) // 최근 것부터 정렬
                .fetchFirst(); // 가장 최근의 하나만 가져오기 == .limit(1).fetchOne();

        return fidPaymentInfo;
    }


    @Transactional(readOnly = true)
    public PaymentInfo getPaymentConfirmation(Long userId, String paymentId) {

        PaymentInfo fidPaymentInfo = queryFactory.selectFrom(paymentInfo)
                .where(paymentInfo.user.id.eq(userId),
                        paymentInfo.partnerOrderId.eq(paymentId))//결제 대기
                .orderBy(paymentInfo.createdDate.desc()) // 최근 것부터 정렬
                .fetchFirst(); // 가장 최근의 하나만 가져오기 == .limit(1).fetchOne();

        return fidPaymentInfo;

    }

    @Transactional
    public void updateAccountBalanceAfterPurchase(Long userId, int pointOfPurchase) {
        RBUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        int totalPoints = pointOfPurchase; // 기본적으로 구매한 포인트로 시작

        // 현재 사용자의 최근 UserPoint 객체 조회
        UserPoint currentUserPoint = queryFactory
                .selectFrom(userPoint)
                .where(userPoint.user.id.eq(userId))
                .orderBy(userPoint.earnedDate.desc())
                .fetchFirst();

        // currentUserPoint가 null이 아니면, 총 포인트 계산
        if (currentUserPoint != null) {
            totalPoints += currentUserPoint.getTotalPoints();
        }

        // 새로운 UserPoint 객체 생성 및 저장
        UserPoint newUserPoint = new UserPoint(user, "포인트 구입", pointOfPurchase, LocalDateTime.now(), totalPoints);
        userPointRepository.save(newUserPoint);
    }


    public String requestPaymentReady(KakaoPaymentReadyRequestDTO paymentRequestDTO) throws Exception {
        String url = "https://" + kakaoPayConfig.getHost() + kakaoPayConfig.getReadyRequestUri();

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
