package com.labmate.riddlebox.api;

import com.labmate.riddlebox.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;


@Controller
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @ResponseBody
    @PostMapping("/oauth2/code")
    public ResponseEntity<String> exchangeCodeForToken(@RequestParam("code") String code,
                                                       @RequestParam("redirectUri") String redirectUri) {

        String tokenResponse = oAuthService.exchangeCodeForToken(code, redirectUri);

        return ResponseEntity.ok(tokenResponse);
    }



}
