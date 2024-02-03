package com.labmate.riddlebox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/support")
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



    /* 1:1 List */
    @GetMapping("/inquiry")
    public String showInquiryList(Model model) {
        model.addAttribute("pageType","inquiryList");
        //List<CommonListDto> commonListDto

        return "layout/layout_base";
    }


    /* 1:1 상세보기 */
    @GetMapping("/inquiry/{inquiryId}")
    public String showInquiryDetail(@PathVariable("inquiryId") Long inquiryId,
                                    Model model) {
        //해당 글의 작성자 id가 현재 세션의 사용자 id인지 확인해야함
        model.addAttribute("pageType","inquiryDetail");

        return "layout/layout_base";
    }



    /* 1:1 작성하기 */
    @GetMapping("/inquiry/new")
    public String createInquiry(Model model) {
        //해당 글의 작성자 id가 현재 세션의 사용자 id인지 확인해야함
        model.addAttribute("pageType","createInquiry");
        model.addAttribute("pageMode","new");

        return "layout/layout_base";
    }

    /* 1:1 제출하기 */
    @PostMapping("/inquiry/submit")
    public String sendInquiry(Model model) {
        //해당 글의 작성자 id가 현재 세션의 사용자 id인지 확인해야함
        model.addAttribute("pageType","inquiryList");

        return "layout/layout_base";
    }


    /* 1:1 수정하기 */
    @GetMapping("/inquiry/modify/{inquiryId}")
    public String updateInquiry(@PathVariable("inquiryId") Long inquiryId,
                                Model model) {
        //해당 글의 작성자 id가 현재 세션의 사용자 id인지 확인해야함
        //글상태가 답변완료면 수정 불가
        //InquiryDetailDto
        model.addAttribute("pageType","updateInquiry");

        return "layout/layout_base";
    }



}
