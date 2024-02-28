package com.labmate.riddlebox.api;

import com.labmate.riddlebox.service.OAuthService;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;


@Controller
public class OAuthController {


    private final OAuthService oAuthService;

    @Autowired
    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @ResponseBody
    @PostMapping("/oauth2/code")
    public ResponseEntity<String> exchangeCodeForToken(@RequestParam("code") String code,
                                                       @RequestParam("redirectUri") String redirectUri) {

        String tokenResponse = oAuthService.exchangeCodeForToken(code, redirectUri);

        return ResponseEntity.ok(tokenResponse);
    }



}
