package com.labmate.riddlebox.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final StaticResourceConfig staticResourceConfig;

    public GlobalControllerAdvice(StaticResourceConfig staticResourceConfig) {
        this.staticResourceConfig = staticResourceConfig;
    }

    @ModelAttribute
    public void globalAttributes(Model model) {
        model.addAttribute("staticUrl", staticResourceConfig.getStaticResourceUrl());
    }
}
