package com.labmate.riddlebox.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.labmate.riddlebox.api.ApiUserController;
import com.labmate.riddlebox.dto.kakaopay.KakaoPayConfig;
import com.labmate.riddlebox.dto.kakaopay.KakaoPayService;
import com.labmate.riddlebox.dto.kakaopay.KakaoPaymentApproveRequestDTO;
import com.labmate.riddlebox.dto.kakaopay.KakaoPaymentApproveResponseDTO;
import com.labmate.riddlebox.entity.PaymentInfo;
import com.labmate.riddlebox.enumpackage.PaymentStatus;
import com.labmate.riddlebox.repository.PaymentInfoRepository;
import com.labmate.riddlebox.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("")
public class PointController {

    private final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    private final KakaoPayConfig kakaoPayConfig;
    private final KakaoPayService kakaoPayService;
    private final PaymentInfoRepository paymentInfoRepository;
    private final ObjectMapper objectMapper;


    public PointController(KakaoPayConfig kakaoPayConfig, KakaoPayService kakaoPayService,
                           PaymentInfoRepository paymentInfoRepository) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        this.kakaoPayConfig = kakaoPayConfig;
        this.kakaoPayService = kakaoPayService;
        this.paymentInfoRepository = paymentInfoRepository;
    }

    /* 포인트 상점 */
    @GetMapping("/point/point-store")
    public String showPointStore(Model model) {
        model.addAttribute("pageType", "pointPurchase");
        model.addAttribute("title", "pointPurchase");
        return "layout/layout_base";
    }


    /* 포인트 구입 완료 화면 */
    @GetMapping("/point/payment-completed")
    public String showPointPaymentCompletedPage(@RequestParam(value = "newPoint", required = false) Integer newPoint,
                                                String paymentResult, Model model) {
        if (newPoint == null) {
            newPoint = 0;
        }
        model.addAttribute("pageType", "paymentCompleted");
        model.addAttribute("title", "paymentCompleted");
        model.addAttribute("newPoint", newPoint);
        model.addAttribute("paymentResult", paymentResult);
        return "layout/layout_base";
    }


    //카카오페이의 응답(성공, 취소, 실패)을 다루기
    @Transactional
    @GetMapping("/kakaopay/approval_url")
    public String handleKakaoPayResponse(@RequestParam(value = "pg_token", required = false) String pgToken,
                                          HttpServletRequest request, Model model) throws JsonProcessingException {
        // 현재 로그인한 사용자 ID를 통해 대기 중인 결제 정보 조회
        Long currentUserId = SecurityUtils.getCurrentUserId();
        PaymentInfo paymentInfo = kakaoPayService.findAwaitingPaymentPaymentInfo(currentUserId);

        // pg_token 유무 확인
        if (pgToken == null || pgToken.isEmpty()) {
            paymentInfo.updatePaymentStatus(PaymentStatus.FAILED);
            paymentInfoRepository.save(paymentInfo);

            // pg_token이 없는 경우, 결제 실패로 간주하고 사용자에게 안내
            model.addAttribute("pageType", "paymentCompleted");
            model.addAttribute("title", "paymentCompleted");
            model.addAttribute("paymentResult", "Fail");
            return "layout/layout_base"; // 결제 실패 페이지로 리다이렉트
        }

        // pg_token이 있는 경우, 결제 승인 처리 진행
        String tid = paymentInfo.getTid();
        String partner_order_id = paymentInfo.getPartnerOrderId();
        String partner_user_id = paymentInfo.getPartnerUserId();
        String cid = kakaoPayConfig.getCid(); //

        paymentInfo.updatePaymentStatus(PaymentStatus.AUTHORIZED);
        paymentInfoRepository.save(paymentInfo);

        String pg_token = request.getParameter("pg_token");

        logger.debug("\n");
        logger.debug(" ===================  4  =================== ");
        logger.debug("pg_token: {}", pg_token);
        logger.debug("\n");


        // 카카오페이 결제 승인 요청
        KakaoPaymentApproveRequestDTO approvalDTO = new KakaoPaymentApproveRequestDTO(cid, tid, partner_order_id, partner_user_id, pg_token);
        RestTemplate restTemplate2 = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayConfig.getSecretKeyDev());

        HttpEntity<KakaoPaymentApproveRequestDTO> entity = new HttpEntity<>(approvalDTO, headers);

        logger.debug("\n");
        logger.debug(" ===================  5  =================== ");
        logger.debug("HttpEntity<String> entity: {}", entity);
        logger.debug("\n");

        ResponseEntity<String> response = restTemplate2.postForEntity(kakaoPayConfig.getApproveRequestUriCompletion(), entity, String.class);

        logger.debug("\n");
        logger.debug(" ===================  6  =================== ");
        logger.debug("Response Status Code: {}", response.getStatusCode());
        logger.debug("Response Body: {}", response.getBody());
        logger.debug("\n");

        String responseBody = response.getBody();

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

            // 결제 승인 성공, 결제 정보 업데이트
            KakaoPaymentApproveResponseDTO completionDTO = objectMapper.readValue(responseBody, KakaoPaymentApproveResponseDTO.class);
            paymentInfo.thirdPaymentInformation(completionDTO.getAid(), completionDTO.getAmount(), completionDTO.getCardInfo(), completionDTO.getApprovedAt());
            paymentInfo.updatePaymentStatus(PaymentStatus.APPROVED);
            paymentInfoRepository.save(paymentInfo);

            //포인트 저장
            kakaoPayService.updateAccountBalanceAfterPurchase(currentUserId, paymentInfo.getTotalPointAmount());

            model.addAttribute("pageType", "paymentCompleted");
            model.addAttribute("title", "paymentCompleted");
            model.addAttribute("newPoint", paymentInfo.getTotalPointAmount());
            model.addAttribute("paymentResult", "Success");

            return "layout/layout_base";  // 결제 성공 페이지로 리다이렉트
        } else {
            // 결제 승인 실패 처리
            paymentInfo.updatePaymentStatus(PaymentStatus.FAILED);
            paymentInfoRepository.save(paymentInfo);

            model.addAttribute("pageType", "paymentCompleted");
            model.addAttribute("title", "paymentCompleted");
            model.addAttribute("paymentResult", "Fail");

            return "layout/layout_base"; // 결제 실패 페이지로 리다이렉트
        }

}

//
//    @Setter(onMethod_ = @Autowired)
//    private KakaoPay kakaopay;
//
//    @GetMapping("/kakaoPay")
//    public void kakaoPayGet() {
//
//    }
//
//    @PostMapping("/kakaoPay")
//    public String kakaoPay() {
//        logger.info("kakaoPay post............................................");
//
//        return "redirect:" + kakaopay.kakaoPayReady();
//
//    }
//
//    @GetMapping("/kakaoPaySuccess")
//    public void kakaoPaySuccess(@RequestParam("pg_token") String pg_token, Model model) {
//        logger.info("kakaoPaySuccess get............................................");
//        logger.info("kakaoPaySuccess pg_token : " + pg_token);
//
//    }


//    @PostMapping("/pay-kakaoPay")
//    public String kakaoPay() throws SQLException {
//
//
//        logger.debug("kakaoPay post............................................");
//
//        // 결제 요청 method 호출
//        return "redirect:" + kakaopay.kakaoPayReady(resultPrice, uPoint, mbNum, useCouponId);
//
//
//    }
//
//    public String kakaoPayReady() throws SQLException {
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        // 서버로 요청할 Header
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "KakaoAK " + "ac83ec5a8b8bb9ce4c1b05b2959816ef");
//        headers.add("Accept", org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
//        headers.add("Content-Type", org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
//
//        // 서버로 요청할 Body
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//        params.add("cid", "TC0ONETIME"); // 가맹정 코드 (test는 카카오에서 제공하는 샘플 코드 사용)
//        params.add("partner_order_id", "1001"); // 주문 번호
//        params.add("partner_user_id", "gorany"); // 주문자명
//        params.add("item_name", "TICKET"); // 상품 이름
//        params.add("quantity", "1"); // 상품 수량
//        params.add("total_amount", "10000"); // 결제 금액
//        params.add("tax_free_amount", "0");
//        params.add("approval_url", "http://localhost:8080/point/payment-completed"); //결제 완료 시 이동 페이지
//        params.add("cancel_url", "http://localhost:8080/point/payment-completed"); // 결제 취소 시 이동 페이지
//        params.add("fail_url", "http://localhost:8080/point/payment-completed"); // 결제 실패 시 이동 페이지
//
//        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);
//
//        try {
//            // Kakao 결제 요청 페이지 호출
//            kaKaoRequestDto = restTemplate.postForObject(new URI(HOST + "/v1/payment/ready"), body, KaKaoRequestDto.class);
//
//            logger.debug("" + kaKaoRequestDto);
//
//            // 결제 성공 시 페이지 이동
//            return kaKaoRequestDto.getNext_redirect_pc_url();
//
//        } catch (RestClientException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return "/pay";
//
//    }
//
//    public ApproveResponseVO kakaoPayInfo(String pg_token) {
//
//        logger.debug("KakaoPayInfoVO............................................");
//        logger.debug("-----------------------------");
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        // 서버로 요청할 Header
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "KakaoAK " + "ac83ec5a8b8bb9ce4c1b05b2959816ef");
//        headers.add("Accept", org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
//        headers.add("Content-Type", org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//        params.add("cid", "TC0ONETIME");
//        params.add("tid", readyResponseVO.getTid());
//        params.add("partner_order_id", "1001");
//        params.add("partner_user_id", "gorany");
//        params.add("pg_token", pg_token);
//        params.add("total_amount", String.valueOf(totalPrice));
//
//        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);
//
//        try {
//
//            approveResponseVO = restTemplate.postForObject(new URI(HOST + "/v1/payment/approve"), body, ApproveResponseVO.class);
//            logger.debug("" + approveResponseVO);
//
//            return approveResponseVO;
//
//        } catch (RestClientException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//

}
