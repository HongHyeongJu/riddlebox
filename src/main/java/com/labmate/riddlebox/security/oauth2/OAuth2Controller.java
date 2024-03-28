package com.labmate.riddlebox.security.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labmate.riddlebox.service.GameService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;


@Controller
public class OAuth2Controller {

    @Autowired
    private GoogleOauth googleOauth;

    @Autowired
    private NaverOauth naverOauth;

    @Autowired
    private KakaoOauth kakaoOauth;

    @Autowired
    private WebClient webClient;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    GameService gameService;


    @GetMapping("/oauth2/google")
    public void getGoogleAuthUrl(HttpServletResponse response) throws IOException {
        response.sendRedirect(googleOauth.getOauthRedirectUrl());
    }


    @GetMapping("/oauth2/kakao")
    public void getKakaoAuthUrl(HttpServletResponse response) throws IOException {
        response.sendRedirect(kakaoOauth.getOauthRedirectUrl());
    }


    @GetMapping("/oauth2/naver")
    public void getKNaverAuthUrl(HttpServletResponse response) throws IOException {
        System.out.println("!!!!!!!!!!!!!!!!!!!!"+naverOauth.getOauthRedirectUrl());
        response.sendRedirect(naverOauth.getOauthRedirectUrl());
    }

    //Securing GET /auth/callback/kakao?code=bXQFS1sYVqzNXKOOafH3EZMpa9GootynqFU7RB3ch2GRK0ku6P0cq9X1cMkKPXOaAAABjiyi1NGoblpFv_zasg

    @GetMapping("/auth/callback/kakao")
    public String kakaoCallback(@RequestParam("code")    String code, Model model) throws IOException {
        System.out.println("!!!!!!!!!!!!!!!!!!!!"+"/auth/callback/kakao");

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE); // JSON 형태의 응답을 기대

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOauth.getKakaoClientId());
        params.add("client_secret", kakaoOauth.getKakaoClientSecert());
        params.add("redirect_uri", "http://localhost:8080/auth/callback/kakao");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                                                                    "https://kauth.kakao.com/oauth/token", // 카카오 토큰 요청 URI
                                                                    kakaoTokenRequest,
                                                                    String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthTokenKakao oAuthTokenKakao = null;
        try {
            oAuthTokenKakao = objectMapper.readValue(response.getBody(), OAuthTokenKakao.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        RestTemplate restTemplate2 = new RestTemplate();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers2.add("Accept", MediaType.APPLICATION_JSON_VALUE); // JSON 형태의 응답을 기대
        headers2.add("Authorization", "Bearer " + oAuthTokenKakao.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest2 = new HttpEntity<>(headers2);

        ResponseEntity<String> response2 = restTemplate2.exchange(
                kakaoOauth.getKAKAO_USERINFO_REQUEST_URI(),
                HttpMethod.POST,
                kakaoTokenRequest2,
                String.class);

        String userInfoJson = response2.getBody();
        boolean loginSuccess = customOAuth2UserService.customLoadOAuth2UserForKaKao(userInfoJson);


        if (loginSuccess) {
            return "redirect:/index";
        } else {
            // 실패 시 로그인 페이지로 리디렉션
            return "redirect:/login";
        }

    }


    //Securing GET /auth/callback/google?code=4%2F0AeaYSHDXhTbRKBtI9kY6YFzpPKDfoqKpZdKIc48b9_Hlqgx67bVQ-DMeIPsc-7kb-FNPPg&scope=email+profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+openid+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&authuser=0&prompt=consent


    @GetMapping("/auth/callback/google")
    public String googleCallback(@RequestParam("code") String code, Model model) throws IOException {
        System.out.println("!!!!!!!!!!!!!!!!!!!!"+"/auth/callback/google");

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleOauth.getGoogleClientId());
        params.add("client_secret", googleOauth.getGoogleClientSecert());
        params.add("redirect_uri", "http://localhost:8080/auth/callback/google");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> googleRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                googleRequest,
                String.class);

        // 액세스 토큰 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(response.getBody());
        String accessToken = node.get("access_token").asText();

        // 액세스 토큰을 사용하여 사용자 정보 요청
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.add("Authorization", "Bearer " + accessToken);
        HttpEntity<?> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                userInfoRequest,
                String.class);

        String userInfoJson = userInfoResponse.getBody();
        /*인증*/
        boolean loginSuccess = customOAuth2UserService.customLoadOAuth2UserForGoogle(userInfoJson);

        if (loginSuccess) {
            return "redirect:/index";
        } else {
            // 실패 시 로그인 페이지로 리디렉션
            return "redirect:/login";
        }

    }


//ecuring GET /login/oauth2/code/naver?code=BSsbYmPryJsM4VLcBv&state=%EC%9E%84%EC%9D%98%EC%9D%98%EC%83%81%ED%83%9C%EA%B0%92


    @GetMapping("/auth/callback/naver")
    public String naverCallback(@RequestParam("code") String code) throws IOException {
        System.out.println("!!!!!!!!!!!!!!!!!!!!"+"/auth/callback/naver");

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE); // JSON 형태의 응답을 기대

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverOauth.getNaverClientId()); // 네이버 클라이언트 ID로 교체
        params.add("client_secret", naverOauth.getNaverClientSecert()); // 네이버 클라이언트 시크릿으로 교체
        params.add("redirect_uri", "http://localhost:8080/auth/callback/naver");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://nid.naver.com/oauth2.0/token", // 네이버 토큰 요청 URI
                naverTokenRequest,
                String.class);

        // 네이버에서 반환한 응답(JSON 형태의 문자열)을 그대로 반환하여 화면에서 확인
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthTokenKakao oAuthTokenNaver = null;
        try {
            oAuthTokenNaver = objectMapper.readValue(response.getBody(), OAuthTokenKakao.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        RestTemplate restTemplate2 = new RestTemplate();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers2.add("Accept", MediaType.APPLICATION_JSON_VALUE); // JSON 형태의 응답을 기대
        headers2.add("Authorization", "Bearer " + oAuthTokenNaver.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> naverTokenRequest2 = new HttpEntity<>(headers2);

        ResponseEntity<String> response2 = restTemplate2.exchange(
                naverOauth.getNAVER_USERINFO_REQUEST_URI(),
                HttpMethod.POST,
                naverTokenRequest2,
                String.class);
        System.out.println("s--------네이버에서 준 유저 정보"+response2.getBody());
        String userInfoJson = response2.getBody();

        /*인증*/
        boolean loginSuccess = customOAuth2UserService.customLoadOAuth2UserForNaver(userInfoJson);

        if (loginSuccess) {
            return "redirect:/index";
        } else {
            // 실패 시 로그인 페이지로 리디렉션
            return "redirect:/login";
        }
    }

}

    /*
   @GetMapping("/auth/callback/naver")
   @ResponseBody
    public String naverCallback(@RequestParam("code") String code, Model model) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE); // JSON 형태의 응답을 기대

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverOauth.getNaverClientId());
        params.add("client_secret", naverOauth.getNaverClientSecert());
        params.add("redirect_uri", "http://localhost:8080/auth/callback/naver");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://nid.naver.com/oauth2.0/token", // 네이버 토큰 요청 URI
                naverTokenRequest,
                String.class);

    // 액세스 토큰 추출
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(response.getBody());
    String accessToken = rootNode.get("access_token").asText();

    // 사용자 정보 요청
    HttpHeaders userInfoHeaders = new HttpHeaders();
    userInfoHeaders.add("Authorization", "Bearer " + accessToken);
    HttpEntity<?> userInfoRequest = new HttpEntity<>(userInfoHeaders);

    ResponseEntity<String> userInfoResponse = restTemplate.exchange(
            "https://openapi.naver.com/v1/nid/me",
            HttpMethod.GET,
            userInfoRequest,
            String.class);

    return userInfoResponse.getBody();

//        ObjectMapper objectMapper = new ObjectMapper();
//        OAuthTokenKakao oAuthTokenKakao = null;
//        try {
//            oAuthTokenKakao = objectMapper.readValue(response.getBody(), OAuthTokenKakao.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        RestTemplate restTemplate2 = new RestTemplate();
//
//        HttpHeaders headers2 = new HttpHeaders();
//        headers2.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers2.add("Accept", MediaType.APPLICATION_JSON_VALUE); // JSON 형태의 응답을 기대
//        headers2.add("Authorization", "Bearer " + oAuthTokenKakao.getAccess_token());
//
//        HttpEntity<MultiValueMap<String, String>> naverTokenRequest2 = new HttpEntity<>(headers2);
//
//        ResponseEntity<String> response2 = restTemplate2.exchange(
//                kakaoOauth.getKAKAO_USERINFO_REQUEST_URI(),
//                HttpMethod.POST,
//                naverTokenRequest2,
//                String.class);

//        String userInfoJson = response2.getBody();
//        return response.getBody();
//        boolean loginSuccess = customOAuth2UserService.customLoadOAuth2UserForKaKao(userInfoJson);


//        if (loginSuccess) {
//            return "redirect:/index";
//        } else {
//            // 실패 시 로그인 페이지로 리디렉션
//            return "redirect:/login";
//        }

    }*/


//    @Autowired
//    private CustomOAuth2UserService customOAuth2UserService;
//
//    @Autowired
//    private CustomAuthenticationManager customAuthenticationManager;
//
//    @GetMapping("/auth/callback/{provider}")
//    public String handleOAuth2Callback(@PathVariable String provider, @RequestParam("code") String code) {
//
//        // 1. provider를 기반으로 인증 코드를 사용해 사용자 정보를 조회 -> 조회값 반환 / 등록&반환
//        OAuth2User oAuth2User = customOAuth2UserService.loadUserByOAuth2UserRequest(provider, code);
//
//        // 3. 사용자를 로그인 상태로 만들기
//        customAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userInfo.getUsername(), null, userInfo.getAuthorities()));
//
//        // 4. 사용자를 /index 페이지로 리디렉션
//        return "redirect:/index";
//    }

 /*   @Autowired
    private OAuthService oAuthService;

    @ResponseBody
//    @PostMapping("/oauth2/code")
    public ResponseEntity<String> exchangeCodeForToken(@RequestParam("code") String code,
                                                       @RequestParam("redirectUri") String redirectUri) {

        String tokenResponse = oAuthService.exchangeCodeForToken(code, redirectUri);

        return ResponseEntity.ok(tokenResponse);
    }*/

/*구글 로그인 버튼 클릭*/
/*구글의 인증 서버로 리디렉션 URL : (타임리프에 적용)*/

/*위의 과정으로 사용자 정보 입력 시 리디렉션 URI 지정으로 보내짐*/

//    @GetMapping("/login/oauth2/code/google")
//    public ResponseEntity<String> googleLogin(@RequestParam("code") String code) {
//        System.out.println("===================== GOOGLE LOGIN ==================");
//        System.out.println("Code: " + code);
//
//        // 필요한 커스텀 로직 추가
//        // 예: 인증 코드를 사용하여 액세스 토큰을 요청하고, 받은 액세스 토큰으로 사용자 정보를 조회한 뒤 처리
//
//        // 로그인 성공 후 리다이렉트할 페이지 URL 반환
//        return ResponseEntity.ok("로그인 성공 후 리다이렉트할 페이지 URL");
//    }
//
//
//    @GetMapping("/login/oauth2/code/google")
//    public String googleLogin2(@RequestParam String code) {
//        System.out.println("===================== GOOGLE LOGIN ==================");
//        System.out.println(code.toString());
//        // 필요한 커스텀 로직 추가
//        // 예: 인증 코드를 사용하여 액세스 토큰을 요청하고, 받은 액세스 토큰으로 사용자 정보를 조회한 뒤 처리
//
//        // 로그인 성공 후 리다이렉트할 페이지 URL 반환
//        return "plz";
//    }

//    @ResponseBody
//    @PostMapping("/login/oauth2/code/google")
//    public void redirectURLForOAuth() {
//        System.out.println("하이");
//        /*인증 코드 추출
//        * 구글의 토큰 엔드포인트로 요청을 보내 액세스 토큰을 요청
//        * 위의 요청으로 받기 ( 인증 코드, 클라이언트 ID, 클라이언트 시크릿, 리디렉션 URI, 그랜트 타입)
//        *
//        * 구글이 액세스 토큰(및 선택적으로 리프레시 토큰)을 웹사이트로 응답
//        *
//        * 받은 액세스 토큰으로 리소스 서버에 사용자 정보 요청
//        * 응답 받은 사용자의 프로필 정보(예: 이름, 이메일 등)를 사용하여 당신의 웹사이트에서 사용자의 계정을 찾거나 새로 생성
//        * */
//
//    }

