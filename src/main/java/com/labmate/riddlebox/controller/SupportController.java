package com.labmate.riddlebox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/supports")
public class SupportController {


    /* 공지, 이벤트, FAQ, 1:1 모아둔 페이지 */
    @GetMapping("")
    public String supportHome(Model model) {
        model.addAttribute("pageType","supportHome");
        model.addAttribute("title","Support Home");

        return "layout/layout_base";
    }


    /* 공지사항 List */
    @GetMapping("/notice")
    public String showNoticeList(@RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                 Model model) {

        if(currentPage==null){currentPage=1;}
        //List<CommonListDto> commonListDto =

        model.addAttribute("pageType","noticeList");
        model.addAttribute("title","Notice List");

        return "layout/layout_base";
    }


    /* 공지사항 상세 글 보기 */
    @GetMapping("/notice/detail/{noticeId}")
    public String showNoticeDetail(@PathVariable("noticeId") Long noticeId,
                                   Model model) {
        //noticeId로 글 찾아오기
        //NoticeDetailDto dto = ...

        model.addAttribute("pageType","noticeDetail");
        model.addAttribute("title","Notice");
        model.addAttribute("middleContent","board");

        return "layout/layout_base";
    }


    /* 이벤트 페이지 */
    @GetMapping("/event")
    public String showEventPage(Model model) {
        model.addAttribute("pageType","eventPage");
        model.addAttribute("title","EVENT");

        return "layout/layout_base";
    }



    /* FAQ List */
    @GetMapping("/faq")
    public String showFAQList(Model model) {
        model.addAttribute("pageType","faqList");
        model.addAttribute("title","FAQ List");

        //List<CommonListDto> commonListDto

        return "layout/layout_base";
    }


    /* inquiry 문의글은 따로 컨트롤러 뺌 */


}
