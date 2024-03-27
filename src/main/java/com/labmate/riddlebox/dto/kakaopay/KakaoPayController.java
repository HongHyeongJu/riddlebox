package com.labmate.riddlebox.dto.kakaopay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
        String partnerOrderId = generateOrderId(userId);

        PaymentRequestDTO requestDTO = PaymentRequestDTO.builder()
                .cid(kakaoPayConfig.getCid())
                .partner_order_id(partnerOrderId)
                .partner_user_id(userEmail)
                .item_name("RiddleBox 포인트")
                .item_code("RB-Point")
                .quantity(1)
                .total_amount(total_amount)
                .tax_free_amount(0)
                .approval_url("http://localhost:8080/kakaopay/approval_url")
                .cancel_url("http://localhost:8080/kakaopay/cancel_url")
                .fail_url("http://localhost:8080/kakaopay/fail_url")
                .build();

        PaymentInfo paymentInfo = new PaymentInfo();
        RBUser user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        paymentInfo.firstPaymentInformation(user, partnerOrderId, userEmail,
                                        "RiddleBox 포인트","RB-Point",
                                                total_amount, totalPointAmount);
        paymentInfo.updatePaymentStatus(PaymentStatus.INITIATED);

        RestTemplate restTemplate1 = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "DEV_SECRET_KEY " + kakaoPayConfig.getSecretKeyDev());

        // PaymentRequestDTO 객체를 JSON 문자열로 변환
        String jsonRequest;
        try {
            jsonRequest = objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting PaymentRequestDTO to JSON", e);
        }

        System.out.println("  ");
        System.out.println("===================1===================");
        System.out.println("  ");
        // 요청 헤더 출력
        System.out.println("Request Headers: " + headers.toString());

        // 요청 본문 출력
        System.out.println("Request Body: " + jsonRequest);
        System.out.println("  ");
        System.out.println("=======================================");
        System.out.println("  ");

        String kakaopayRequestUrl = "https://" + kakaoPayConfig.getHost() + kakaoPayConfig.getRequestUri(); //"https://open-api.kakaopay.com/online/v1/payment/ready HTTP/1.1"
        String requestUrl = "https://open-api.kakaopay.com/online/v1/payment/ready";
        HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);
        paymentInfoRepository.save(paymentInfo);

        System.out.println("  ");
        System.out.println("==================3===================");
        System.out.println("  ");
        System.out.println("HttpEntity<String> entity: " + entity.toString());
        System.out.println("  ");
        System.out.println("=======================================");
        System.out.println("  ");


        //[1] restTemplate.postForEntity(url, new HttpEntity<>(jsonRequest, headers), String.class);
        //[2] restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(jsonRequest, headers), String.class);
        // POST 요청 보내기
        ResponseEntity<String> response =  restTemplate1.postForEntity(requestUrl,
                                                                                entity,
                                                                                String.class);

        // 응답 상태 코드 및 본문 출력
        System.out.println("  ");
        System.out.println("==================3===================");
        System.out.println("  ");
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
        System.out.println("  ");
        System.out.println("=======================================");
        System.out.println("  ");

        // 응답 처리
        String responseBody = String.valueOf(response.getBody());
        paymentInfo.updatePaymentStatus(PaymentStatus.AWAITING_PAYMENT);
        paymentInfoRepository.save(paymentInfo);

        // JSON 응답을 PaymentResponseDTO 객체로 변환
        PaymentResponseDTO responseDTO = objectMapper.readValue(responseBody, PaymentResponseDTO.class);
        paymentInfo.secondPaymentInformation(responseDTO.getTid(), responseDTO.getCreated_at());


        String nextRedirectPcUrl = responseDTO.getNext_redirect_pc_url();

        return ResponseEntity.ok(Collections.singletonMap("nextRedirectPcUrl ", nextRedirectPcUrl));
    }



    //성공페이지에서 받을 것
    @Transactional
    @PostMapping("/kakaopay/approval_url")
    public ResponseEntity<?> kakaoPayRequest_Success(HttpServletRequest request) throws JsonProcessingException {
        // pg_token 추출
        Long currentUserId = SecurityUtils.getCurrentUserId();
        PaymentInfo paymentInfo = kakaoPayService.findAwaitingPaymentPaymentInfo(currentUserId);

        String tid = paymentInfo.getTid();
        String partner_order_id = paymentInfo.getPartnerOrderId();
        String partner_user_id = paymentInfo.getPartnerUserId();
        String cid = kakaoPayConfig.getCid(); // 예시 CID, 실제 사용 시 변경 필요

        paymentInfo.updatePaymentStatus(PaymentStatus.AUTHORIZED);
        paymentInfoRepository.save(paymentInfo);

        String pg_token = request.getParameter("pg_token");

        // PaymentApprovalDTO 객체 생성
        PaymentApprovalDTO approvalDTO = new PaymentApprovalDTO(cid, tid, partner_order_id, partner_user_id, pg_token);

        // 결제 승인 요청 로직 구현 필요 (예: RestTemplate 사용)
        // 예제 코드
        RestTemplate restTemplate2 = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "DEV_SECRET_KEY " + kakaoPayConfig.getSecretKeyDev());

        HttpEntity<PaymentApprovalDTO> entity = new HttpEntity<>(approvalDTO, headers);
        ResponseEntity<String> response = restTemplate2.postForEntity("https://open-api.kakaopay.com/v1/payment/approve?pg_token"+pg_token, entity, String.class);

        // 응답 처리
        String responseBody = response.getBody();
        // JSON 응답을 PaymentResponseDTO 객체로 변환
        PaymentCompletionDTO completionDTO = objectMapper.readValue(responseBody, PaymentCompletionDTO.class);
        paymentInfo.thirdPaymentInformation(completionDTO.getAid(), completionDTO.getAmount(), completionDTO.getCardInfo(), completionDTO.getApprovedAt());

        paymentInfo.updatePaymentStatus(PaymentStatus.APPROVED);
        paymentInfoRepository.save(paymentInfo);

        String redirectUrl = "/point/payment-completed?"+paymentInfo.getTotalPointAmount(); // 정적 리소스나 외부 URL
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();

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
