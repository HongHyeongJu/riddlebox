package com.labmate.riddlebox.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController {

    @GetMapping("/error/custom-403-page")
    public String custom403(Model model) {
        model.addAttribute("errorTitle", "접근불가");
        model.addAttribute("errorMsg", "이 페이지에 접근할 권한이 없습니다.");
        return "error/error_base"; // 실제 403 에러 페이지로 이동
    }

    @RequestMapping("/custom-error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorTitle", "페이지를 찾을 수 없습니다");
                model.addAttribute("errorMsg", "요청하신 페이지는 존재하지 않습니다.");
            } else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorTitle", "서버 내부 오류");
                model.addAttribute("errorMsg", "서버 내부에서 오류가 발생했습니다.");
            } else {
                model.addAttribute("errorTitle", "알 수 없는 오류");
                model.addAttribute("errorMsg", "알 수 없는 오류가 발생했습니다.");
            }
        }

        return "error/error_base"; // 에러 페이지 경로
    }




}