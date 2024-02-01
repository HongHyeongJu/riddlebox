package com.labmate.riddlebox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MyPageController {

    /*내 게임 기록, 1:1 문의, 나의 정보(회원정보 수정), 내 프로필, 등급*/
    @GetMapping("")
    public String myPageHome(Model model) {
        model.addAttribute("pageType","myPageHome");
        model.addAttribute("title", "RiddleBox MyPage");

        return "layout/layout_base"; // HTML 뷰 이름
    }



    /* 내 게임 기록 보여주기*/
    @GetMapping("/game-records")
    public String myGameRecords(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long memberId = authentication.getMemberId();
        Long testMemberId = 1L;
        model.addAttribute("pageType","myPageDetail");
        model.addAttribute("title", "RiddleBox MyRecords");

        // 게임 기록 로드 및 처리
//        List<GameRecord>
        //TODO 게임 기록 보여줄 DTO만들기

        return "layout/layout_base"; // HTML 뷰 이름

    }


    /* 나의 정보 (회원정보 수정) */
    @GetMapping("/my-point")
    public String myPoint(Model model) {
        // 사용자 정보 로드 및 처리
        model.addAttribute("pageType","myPageDetail");
        model.addAttribute("title", "RiddleBox MyPoint");

        return "layout/layout_base"; // HTML 뷰 이름

    }

    /* 1:1 문의 */
    @GetMapping("/inquiries")
    public String myInquiries(Model model) {
    //        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
        // 1:1 문의 정보 로드 및 처리
        //TODO 문의내용 Dto는 이미 있다 -> 그러나 적합한지 확인하기
        model.addAttribute("pageType","myPageInquiry");

        return "layout/layout_base"; // HTML 뷰 이름

    }


    /* 나의 정보 (회원정보 수정) */
    @GetMapping("/my-info")
    public String myInfo(Model model) {
        // 사용자 정보 로드 및 처리
        model.addAttribute("pageType","myPageInfo");

        return "layout/layout_base"; // HTML 뷰 이름

    }


}
