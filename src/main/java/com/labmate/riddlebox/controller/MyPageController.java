package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.dto.MyPageDto;
import com.labmate.riddlebox.dto.MyPageProfileDto;
import com.labmate.riddlebox.dto.MyRecordDto;
import com.labmate.riddlebox.dto.UserAnswerDto;
import com.labmate.riddlebox.security.PrincipalDetails;
import com.labmate.riddlebox.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;


    //@ModelAttribute를 사용하여 MyPageDto를 모든 뷰에 자동으로 추가
    @ModelAttribute("MyPageDto")
    public MyPageDto myPageDto() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) auth.getPrincipal();
            Long userId = principalDetails.getUserPK();
            return myPageService.getUserMyPageDto(userId);
        }
        return null; // 적절한 예외 처리나 기본 값 반환을 고려해야 할 수 있음
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
    public String myGameRecords(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principalDetails = (PrincipalDetails) auth.getPrincipal();
        Long userId = principalDetails.getUserPK();

        List<MyRecordDto> myRecordDtoList = myPageService.getUserRecordDtoList(userId);
        model.addAttribute("myPageType", "record");
        model.addAttribute("myRecordDtoList", myRecordDtoList);

        return "layout/layout_base"; // HTML 뷰 이름
    }


    /* 나의 포인트 */
    @GetMapping("/point")
    public String myPoint(Model model) {
        // 사용자 정보 로드 및 처리
        model.addAttribute("myPageType", "point");


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
