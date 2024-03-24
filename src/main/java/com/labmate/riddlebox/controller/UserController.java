package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.dto.SignupRequestDto;
import com.labmate.riddlebox.service.EmailService;
import com.labmate.riddlebox.service.RedisService;
import com.labmate.riddlebox.service.UserService;
import com.labmate.riddlebox.util.CustomException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        model.addAttribute("title", "RiddleBox Signup");
        model.addAttribute("signupRequestDto", new SignupRequestDto());
        return "layout/layout_base";
    }


    /* 회원가입 */
    @PostMapping("/signup")
    public String signupNewUser(@ModelAttribute("signupRequestDto") @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageType", "signup");
            model.addAttribute("title", "RiddleBox Signup");
            model.addAttribute("signupRequestDto", signupRequestDto);
            return "layout/layout_base";
        }

        if (!signupRequestDto.getPassword1().equals(signupRequestDto.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            model.addAttribute("pageType", "signup");
            model.addAttribute("title", "RiddleBox Signup");
            model.addAttribute("signupRequestDto", signupRequestDto);
            return "layout/layout_base";
        }

        try {
            userService.signupNewUser(signupRequestDto);
        } catch (CustomException e) {
            System.out.println("===========CustomException==============");
            System.out.println(e.getErrorCode());
            System.out.println(e.getMessage());
            model.addAttribute("pageType", "signup");
            model.addAttribute("title", "RiddleBox Signup");
            model.addAttribute("signupRequestDto", signupRequestDto);
            return "layout/layout_base";
        }

        // 회원가입 완료 후 로그인 페이지 또는 인덱스 페이지로 리디렉션
        return "redirect:/index";
    }
    /* 로그인 */
//    @PostMapping("/login")
//    public String loginBasic(Model model) {
//        model.addAttribute("pageType", "login");
//        model.addAttribute("title","RiddleBox login");
//        return "layout/layout_base";
//    }


    /* 로그인 */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("pageType", "login");
        model.addAttribute("title", "RiddleBox login");
        return "layout/layout_base";
    }


    /* 계정 찾기 */
    @GetMapping("/account/recovery")
    public String showAccountRecoveryPage(Model model) {
        model.addAttribute("pageType", "findAccount");
        model.addAttribute("title", "account-recovery");
        return "layout/layout_base";
    }


}
