package com.labmate.riddlebox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController {

    @GetMapping("/error/custom-403-page")
    public String custom403(Model model) {
        model.addAttribute("errorTitle", "접근불가");
        model.addAttribute("errorMsg", "이 페이지에 접근할 권한이 없습니다.");
        return "error/error_base"; // 실제 403 에러 페이지로 이동
    }
}