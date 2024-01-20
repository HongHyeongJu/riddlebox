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

//    @GetMapping("/")
//    public String getGameStory(Model model) {
//        Long gameId= 1L;
//        GameplayInfoDto gameplayInfoDto = gameService.findGameInfos(gameId);
//        model.addAttribute("gameplayInfoDto", gameplayInfoDto);
////        return "gamePlayStory";
//        return "test";
//    }

}
