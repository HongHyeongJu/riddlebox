package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.dto.GameplayInfoDto;
import com.labmate.riddlebox.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class TestController {

    @Autowired
    GameService gameService;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }



    @GetMapping("/test/this-event")
    public String helloEvent() {
        return "test_html/event";
    }


}
