package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.service.EmailService;
import com.labmate.riddlebox.service.RedisService;
import com.labmate.riddlebox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    private static final String AUTH_CODE_PREFIX = "AuthCode_";

    /* 회원가입 */
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("pageType", "signup");
        model.addAttribute("title","RiddleBox Signup");
        return "layout/layout_base";
    }


    /* 로그인 */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("pageType", "login");
        model.addAttribute("title","RiddleBox login");
        return "layout/layout_base";
    }


    /* 계정 찾기 */
    @GetMapping("/account/recovery")
    public String showAccountRecoveryPage(Model model) {
        model.addAttribute("pageType", "findAccount");
        model.addAttribute("title","account-recovery");
        return "layout/layout_base";
    }



}
