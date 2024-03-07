package com.labmate.riddlebox.api;

//import com.labmate.riddlebox.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping(produces = "application/json")
public class OAuthController {

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

}
