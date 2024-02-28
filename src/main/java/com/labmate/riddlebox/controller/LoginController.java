package com.labmate.riddlebox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("pageType", "login");
        model.addAttribute("title","RiddleBox login");
        return "layout/layout_base";
    }





}
