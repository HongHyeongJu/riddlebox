package com.labmate.riddlebox.dto.kakaopay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.labmate.riddlebox.api.ApiUserController;
import com.labmate.riddlebox.config.StaticResourceConfig;
import com.labmate.riddlebox.dto.OrderInfoDTO;
import com.labmate.riddlebox.entity.PaymentInfo;
import com.labmate.riddlebox.entity.RBUser;
import com.labmate.riddlebox.enumpackage.PaymentStatus;
import com.labmate.riddlebox.repository.PaymentInfoRepository;
import com.labmate.riddlebox.repository.UserRepository;
import com.labmate.riddlebox.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.kakaoPayConfig = kakaoPayConfig;
        this.kakaoPayService = kakaoPayService;
        this.staticResourceConfig = staticResourceConfig;
        this.userRepository = userRepository;
        this.paymentInfoRepository = paymentInfoRepository;
    }

//    public String kakaoPayRequest_02(@RequestBody orderInfoDTO orderInfoDTO) throws JsonProcessingException {


    //    @Autowired
//    private KakaoPayService kakaoPayService;
    @Transactional
    @PostMapping("/api/pay/kakaopay")
    public ResponseEntity<?> processKakaoPayRequest(@RequestBody OrderInfoDTO orderInfoDTO) {
        try {
            //사용자 정보
            String userEmail = SecurityUtils.getCurrentUserEmail();
            Long userId = SecurityUtils.getCurrentUserId();

            // 포인트 및 결제 금액 계산
            int total_amount = orderInfoDTO.getPoint_100_Quantity() * 110 + orderInfoDTO.getPoint_1000_Quantity() * 1000;
            int totalPointAmount = orderInfoDTO.getPoint_100_Quantity() * 100 + orderInfoDTO.getPoint_1000_Quantity() * 1000;

            // 결제 요청 객체 준비
            KakaoPaymentReadyRequestDTO requestDTO = KakaoPaymentReadyRequestDTO.builder()
                                                                                .cid(kakaoPayConfig.getCid())
                                                                                .partner_order_id(orderInfoDTO.getPaymentId())
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

            // 결제 정보 생성
            PaymentInfo paymentInfo = new PaymentInfo();
            RBUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
            paymentInfo.firstPaymentInformation(user, orderInfoDTO.getPaymentId(), userEmail, "RiddleBox 포인트", "RB-Point", total_amount, totalPointAmount);
            paymentInfo.updatePaymentStatus(PaymentStatus.INITIATED);
            paymentInfoRepository.save(paymentInfo);

            // 카카오페이 API 호출
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

            String kakaopayReadyRequestUrl = kakaoPayConfig.getReadyRequestUriCompletion();
            HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

            logger.debug("\n");
            logger.debug(" ===================  2  =================== ");
            logger.debug("HttpEntity<String> entity: {}", entity);
            logger.debug("\n");

            ResponseEntity<String> response = restTemplate1.postForEntity(kakaopayReadyRequestUrl, entity, String.class);

            logger.debug("\n");
            logger.debug(" ===================  3  =================== ");
            logger.debug("Response Status Code: {}", response.getStatusCode());
            logger.debug("Response Body: {}", response.getBody());
            logger.debug("\n");

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("카카오페이 호출 실패");
            }

            // 응답 처리
            String responseBody = String.valueOf(response.getBody());

            // objectMapper.registerModule(new JavaTimeModule());  //생성자에서 일괄주입으로 변경
            KakaoPaymentReadyResponseDTO responseDTO = objectMapper.readValue(responseBody, KakaoPaymentReadyResponseDTO.class);

            paymentInfo.secondPaymentInformation(responseDTO.getTid(), responseDTO.getCreated_at()); //카카오 서버 응답값 저장 (tid, created_at)
            paymentInfo.updatePaymentStatus(PaymentStatus.AWAITING_PAYMENT);
            paymentInfoRepository.save(paymentInfo);

            //사용자에게 카카오 결제 QR 링크 전달 Next_redirect_pc_url
            return ResponseEntity.ok(Collections.singletonMap("nextRedirectPcUrl", responseDTO.getNext_redirect_pc_url()));

        } catch (EntityNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return new ResponseEntity<>("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error: {}", e.getMessage());
            return new ResponseEntity<>("서버 오류로 인해 처리할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            logger.error("Internal server error: {}", e.getMessage());
            return new ResponseEntity<>("내부 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/api/pay/check-status")
    public ResponseEntity<?> checkPaymentStatus(@RequestBody Map<String, String> body) {
        String paymentId = body.get("paymentId");
        // 현재 사용자의 ID를 가져옴
        Long currentUserId = SecurityUtils.getCurrentUserId();
        // 결제 정보 조회
        PaymentInfo paymentInfo = kakaoPayService.getPaymentConfirmation(currentUserId, paymentId);

        if (paymentInfo != null) {

            String paymentStatus = paymentInfo.getStatus() == PaymentStatus.APPROVED ? "Success" : "Failure";
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


    public String requestPaymentReady(KakaoPaymentReadyRequestDTO paymentRequestDTO) throws Exception {
        String url = "https://" + kakaoPayConfig.getHost() + kakaoPayConfig.getReadyRequestUri();

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


// processKakaoPayRequest 리팩토링
/*
@Transactional
@PostMapping("/api/pay/kakaopay")
public ResponseEntity<?> processKakaoPayRequest(@RequestBody OrderInfoDTO orderInfo) throws JsonProcessingException {
    String userEmail = SecurityUtils.getCurrentUserEmail();
    Long userId = SecurityUtils.getCurrentUserId();

    int totalAmount = orderInfo.getPoint100Quantity() * 110 + orderInfo.getPoint1000Quantity() * 1000;
    int totalPoint = orderInfo.getPoint100Quantity() * 100 + orderInfo.getPoint1000Quantity() * 1000;

    KakaoPaymentReadyRequestDTO requestDTO = buildPaymentRequestDTO(orderInfo, userEmail, totalAmount);
    PaymentInfo paymentInfo = initializePaymentInfo(userId, orderInfo, totalPoint);
    ResponseEntity<String> response = sendPaymentRequest(requestDTO);

    if (processPaymentResponse(response, paymentInfo)) {
        return ResponseEntity.ok(Map.of("nextRedirectPcUrl", paymentInfo.getRedirectUrl()));
    } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment processing failed");
    }
}

private KakaoPaymentReadyRequestDTO buildPaymentRequestDTO(OrderInfoDTO orderInfo, String userEmail, int totalAmount) {
    return KakaoPaymentReadyRequestDTO.builder()
        .cid(kakaoPayConfig.getCid())
        .partner_order_id(orderInfo.getPaymentId())
        .partner_user_id(userEmail)
        .item_name("RiddleBox 포인트")
        .quantity(1)
        .total_amount(totalAmount)
        .tax_free_amount(500)
        .approval_url(kakaoPayConfig.getApprovalUrl())
        .cancel_url(kakaoPayConfig.getCancelUrl())
        .fail_url(kakaoPayConfig.getFailUrl())
        .build();
}

private PaymentInfo initializePaymentInfo(Long userId, OrderInfoDTO orderInfo, int totalPoint) {
    PaymentInfo paymentInfo = new PaymentInfo(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found")),
                                              orderInfo.getPaymentId(), "RiddleBox 포인트", totalPoint);
    paymentInfoRepository.save(paymentInfo);
    return paymentInfo;
}

private ResponseEntity<String> sendPaymentRequest(KakaoPaymentReadyRequestDTO requestDTO) throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "KakaoAK " + kakaoPayConfig.getApiKey());

    String requestJson = objectMapper.writeValueAsString(requestDTO);
    logger.debug("Sending payment request: {}", requestJson);

    return restTemplate.postForEntity(kakaoPayConfig.getPaymentRequestUrl(), new HttpEntity<>(requestJson, headers), String.class);
}

private boolean processPaymentResponse(ResponseEntity<String> response, PaymentInfo paymentInfo) throws JsonProcessingException {
    if (response.getStatusCode().is2xxSuccessful()) {
        KakaoPaymentReadyResponseDTO responseDTO = objectMapper.readValue(response.getBody(), KakaoPaymentReadyResponseDTO.class);
        paymentInfo.updateWithResponse(responseDTO);
        paymentInfoRepository.save(paymentInfo);
        logger.debug("Payment processed successfully: {}", response.getBody());
        return true;
    } else {
        logger.error("Payment processing failed: {}", response.getStatusCode());
        return false;
    }
}
*/
