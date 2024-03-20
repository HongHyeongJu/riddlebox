package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @Autowired
    GameService gameService;

    @GetMapping("/test/hello")
    public String hello() {
        return "hello";
    }



    @GetMapping("/test/this-event")
    public String helloEvent() {
        return "test_html/event";
    }


}
