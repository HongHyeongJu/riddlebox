package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.security.PrincipalDetails;
import com.labmate.riddlebox.security.SecurityUtils;
import com.labmate.riddlebox.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;


    //@ModelAttribute를 사용하여 MyPageDto를 모든 뷰에 자동으로 추가
    @ModelAttribute("MyPageDto")
    public MyPageDto myPageDto() {
        Long userId = SecurityUtils.getCurrentUserId();
        return myPageService.getUserMyPageDto(userId);
    }


    // 모든 뷰에 "title"과 "pageType" 추가
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "RiddleBox MyPage");
        model.addAttribute("pageType", "myPageHome");
    }


    /*내 게임 기록, 1:1 문의, 나의 정보(회원정보 수정), 내 프로필, 등급*/
    @GetMapping("")
    public String myPageHome(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principalDetails = (PrincipalDetails) auth.getPrincipal();
        Long userId = principalDetails.getUserPK();

        MyPageProfileDto myPageProfileDto =  myPageService.getMyPageProfileDto(userId);
        model.addAttribute("myPageProfileDto", myPageProfileDto);
        model.addAttribute("myPageType", "profile");

        return "layout/layout_base"; // HTML 뷰 이름
    }


    /* 내 게임 기록 보여주기*/
    @GetMapping("/record")
    public String myGameRecords(@RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "10") int size,
                                Model model) {

        Long userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<MyRecordDto> myRecordDtoPage = myPageService.getUserRecordDtoList(userId, pageable);

        model.addAttribute("myPageType", "record");
        model.addAttribute("myRecordDtoPage", myRecordDtoPage);

        return "layout/layout_base"; // HTML 뷰 이름
    }

    /* 나의 포인트 */
    @GetMapping("/point")
    public String myPoint(Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<MyPointDto> myPointDtoList = myPageService.getUserPointDtoList(userId);

        model.addAttribute("myPageType", "point");
        model.addAttribute("myPointDtoList", myPointDtoList);
        return "layout/layout_base"; // HTML 뷰 이름

    }

    /* 1:1 문의 */
    @GetMapping("/inquiry")
    public String myInquiries(Model model) {
        //        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
        // 1:1 문의 정보 로드 및 처리
        //TODO 문의내용 Dto는 이미 있다 -> 그러나 적합한지 확인하기
        model.addAttribute("myPageType", "inquiry");

        return "layout/layout_base"; // HTML 뷰 이름

    }


    /* 나의 정보 (회원정보 수정) */
    @GetMapping("/profile-update")
    public String myInfo(Model model) {
        // 사용자 정보 로드 및 처리
        model.addAttribute("myPageType", "profile-update");


        return "layout/layout_base"; // HTML 뷰 이름

    }


}
