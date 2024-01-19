package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.entity.Member;
import com.labmate.riddlebox.service.GameService;
import com.labmate.riddlebox.web.argumentresolver.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {


    @Autowired
    GameService gameService;
//    @GetMapping("/")
//    public String homeLoginArgumentResolver(@Login Member loginMember, Model model) {
//
//        //세션에 회원 데이터가 없으면 home
//        if (loginMember == null) {
//            return "home";
//        }
//
//        //세션이 유지되면 로그인으로 이동
//        model.addAttribute("member", loginMember);
//        return "loginHome";
//    }

    @GetMapping("/") //로그인 안한 모든 사용자를 위한 index페이지
    public String Homepage(Model model){
        //로그인여부 확인, 로그인 했으면 -> redirect:/home
//        Authentication 객체는 현재 인증된 사용자의 세부 정보를 포함하고 있으며,
//                              일반적으로 사용자의 이름이나 식별자를 포함하고 있습니다.
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null || authentication.isAuthenticated() ||
//            authentication instanceof AnonymousAuthenticationToken) {
//            // 로그인하지 않은 경우
//            return "redirect:/home";
//        }

        //추천게임 목록 받기
        List<GameListDto> gameListDtos = gameService.fetchRecommendedGamesForHomepage();

        //모델에 담기
        model.addAttribute("gameListDtos", gameListDtos);
        model.addAttribute("pageType", "homepage");
        model.addAttribute("title", "RiddleBox");

        //return index.html
        return "layout/layoutBase";

    }

    /*
    @GetMapping("/home")  //로그인한 사용자의 index페이지
    public String HomepageLoing(Model model){


        //추천게임 Table에서 받아오기 // TODO: 2024-01-16 나중에 데이터가 많아지면 3개월간 조회 많은 순/ 사용자 선호 카테고리+안해본게임


        //이어하기 데이터 1건


        //모델에 담기


        //return index.html

    }*/


}
