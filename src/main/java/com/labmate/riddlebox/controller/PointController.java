package com.labmate.riddlebox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/point")
public class PointController {


    /* 포인트 상점 */
    @GetMapping("/point-store")
    public String showPointStore(Model model) {
        model.addAttribute("pageType", "pointStore");
        model.addAttribute("title", "pointStore");
        return "layout/layout_base";
    }


    /* 포인트 구입 완료 화면 */
    @GetMapping("/payment-completed")
    public String showPointPaymentCompletedPage(@RequestParam(value = "newPoint" , required = false) Integer newPoint, Model model) {
        if(newPoint==null){newPoint = 0;}
        model.addAttribute("pageType", "paymentCompleted");
        model.addAttribute("title", "paymentCompleted");
        model.addAttribute("newPoint", newPoint);
        return "layout/layout_base";
    }

}
