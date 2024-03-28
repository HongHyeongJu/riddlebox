package com.labmate.riddlebox.dto.kakaopay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.labmate.riddlebox.api.ApiUserController;
import com.labmate.riddlebox.config.StaticResourceConfig;
import com.labmate.riddlebox.dto.PurchaseInformation;
import com.labmate.riddlebox.entity.PaymentInfo;
import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.enumpackage.PaymentStatus;
import com.labmate.riddlebox.repository.PaymentInfoRepository;
import com.labmate.riddlebox.repository.UserRepository;
import com.labmate.riddlebox.security.PrincipalDetails;
import com.labmate.riddlebox.security.SecurityUtils;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class KakaoPayController {

    private final KakaoPayConfig kakaoPayConfig;
    private final KakaoPayService kakaoPayService;
    private final StaticResourceConfig staticResourceConfig;
    private final UserRepository userRepository;
    private final PaymentInfoRepository paymentInfoRepository;
    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(ApiUserController.class);


    public KakaoPayController(KakaoPayConfig kakaoPayConfig, KakaoPayService kakaoPayService,
                              StaticResourceConfig staticResourceConfig, UserRepository userRepository,
                              PaymentInfoRepository paymentInfoRepository) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        this.kakaoPayConfig = kakaoPayConfig;
        this.kakaoPayService = kakaoPayService;
        this.staticResourceConfig = staticResourceConfig;
        this.userRepository = userRepository;
        this.paymentInfoRepository = paymentInfoRepository;
    }

//    public String kakaoPayRequest_02(@RequestBody PurchaseInformation purchaseInformation) throws JsonProcessingException {


    //    @Autowired
//    private KakaoPayService kakaoPayService;
    @Transactional
    @PostMapping("/api/pay/kakaopay")
    public ResponseEntity<?> kakaoPayRequest_02(@RequestBody PurchaseInformation purchaseInformation) throws JsonProcessingException {
        //인증객체 꺼내기
        String userEmail = SecurityUtils.getCurrentUserEmail();
        Long userId = SecurityUtils.getCurrentUserId();

        int total_amount = purchaseInformation.getPoint_100_Quantity() * 110 + purchaseInformation.getPoint_1000_Quantity() * 1000;
        int totalPointAmount = purchaseInformation.getPoint_100_Quantity() * 100 + purchaseInformation.getPoint_1000_Quantity() * 1000;
//        String partnerOrderId = generateOrderId(userId);

        PaymentRequestDTO requestDTO = PaymentRequestDTO.builder()
                .cid(kakaoPayConfig.getCid())
                .partner_order_id(purchaseInformation.getPaymentId())
                .partner_user_id(userEmail)
                .item_name("RiddleBox 포인트")
                .item_code("RB-Point")
                .quantity(1)
                .total_amount(total_amount)
                .tax_free_amount(500)
                .approval_url("http://localhost:8080/kakaopay/approval_url")
                .cancel_url("http://localhost:8080/kakaopay/approval_url")
                .fail_url("http://localhost:8080/kakaopay/approval_url")
                .build();

        PaymentInfo paymentInfo = new PaymentInfo();
        RBUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        paymentInfo.firstPaymentInformation(user, purchaseInformation.getPaymentId(), userEmail,
                "RiddleBox 포인트", "RB-Point",
                total_amount, totalPointAmount);
        paymentInfo.updatePaymentStatus(PaymentStatus.INITIATED);

        RestTemplate restTemplate1 = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayConfig.getSecretKeyDev());

        // PaymentRequestDTO 객체를 JSON 문자열로 변환
        String jsonRequest;
        try {
            jsonRequest = objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting PaymentRequestDTO to JSON", e);
        }

        logger.debug("\n");
        logger.debug(" ===================  1  =================== ");
        logger.debug("Request Headers: {}", headers);
        logger.debug("Request Body: {}", jsonRequest);
        logger.debug("\n");

        String kakaopayRequestUrl = "https://" + kakaoPayConfig.getHost() + kakaoPayConfig.getRequestUri(); //"https://open-api.kakaopay.com/online/v1/payment/ready HTTP/1.1"
        String requestUrl = "https://open-api.kakaopay.com/online/v1/payment/ready";
        String requestUrlMockup = "https://online-pay.kakao.com/mockup/online/v1/payment/ready";
        HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);
        paymentInfoRepository.save(paymentInfo);

        logger.debug("\n");
        logger.debug(" ===================  2  =================== ");
        logger.debug("HttpEntity<String> entity: {}", entity);
        logger.debug("\n");


        //[1] restTemplate.postForEntity(url, new HttpEntity<>(jsonRequest, headers), String.class);
        //[2] restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(jsonRequest, headers), String.class);
        // POST 요청 보내기
        ResponseEntity<String> response = restTemplate1.postForEntity(requestUrl,
                entity,
                String.class);

        logger.debug("\n");
        logger.debug(" ===================  3  =================== ");
        logger.debug("Response Status Code: {}", response.getStatusCode());
        logger.debug("Response Body: {}", response.getBody());
        logger.debug("\n");


        // 응답 처리
        String responseBody = String.valueOf(response.getBody());


        // JSON 응답을 PaymentResponseDTO 객체로 변환
        // Java 8 날짜/시간 모듈 등록
//        objectMapper.registerModule(new JavaTimeModule());  //생성자에서 일괄주입
        PaymentResponseDTO responseDTO = objectMapper.readValue(responseBody, PaymentResponseDTO.class);

        paymentInfo.secondPaymentInformation(responseDTO.getTid(), responseDTO.getCreated_at());
        paymentInfo.updatePaymentStatus(PaymentStatus.AWAITING_PAYMENT);
        paymentInfoRepository.save(paymentInfo);

        String nextRedirectPcUrl = responseDTO.getNext_redirect_pc_url();

        return ResponseEntity.ok(Collections.singletonMap("nextRedirectPcUrl", nextRedirectPcUrl));

    }



    @PostMapping("/api/pay/check-status")
    public ResponseEntity<?> checkPaymentStatus(@RequestBody Map<String, String> body) {
        String paymentId = body.get("paymentId");
        // 현재 사용자의 ID를 가져옴
        Long currentUserId = SecurityUtils.getCurrentUserId();
        // 결제 정보 조회
        PaymentInfo paymentInfo = kakaoPayService.getPaymentConfirmation(currentUserId, paymentId);

        if (paymentInfo != null) {

            String paymentStatus = paymentInfo.getStatus()==PaymentStatus.APPROVED ? "Success" : "Failure";
            int totalPointAmount = paymentInfo.getTotalPointAmount();

            // 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("paymentStatus", paymentStatus);
            response.put("totalPointAmount", totalPointAmount);

            return ResponseEntity.ok(response);
        } else {
            // 결제 정보가 없는 경우, 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment information not found.");
        }
    }


    public static String generateOrderId(Long userId) {
        // 주문 시각 포맷팅
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = now.format(formatter);

        // 주문 ID 생성
        return String.format("rb-k-%s-%s", userId, formattedDateTime);
    }

//    approval_url + ?pg_token= 파라미터


    public String requestPaymentReady(PaymentRequestDTO paymentRequestDTO) throws Exception {
        String url = "https://" + kakaoPayConfig.getHost() + kakaoPayConfig.getRequestUri();

        RestTemplate restTemplate = new RestTemplate();

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
