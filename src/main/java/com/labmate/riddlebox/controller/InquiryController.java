package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.entity.Inquiry;
import com.labmate.riddlebox.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/support/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;

    @Autowired
    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }


    /* 문의 목록 */
    @GetMapping("")
    public String listInquiries(Model model) {
        model.addAttribute("pageType","inquiryList");

        return "layout/layout_base";

    }

    /* 문의 상세 조회 */
    @GetMapping("/{inquiryId}")
    public String showInquiry(@PathVariable Long inquiryId, Model model) {
        //해당 글의 작성자 id가 현재 세션의 사용자 id인지 확인해야함
        model.addAttribute("pageType","inquiryDetail");

        return "layout/layout_base";
    }

    /* 문의 생성 폼 */
    @GetMapping("/new")
    public String createInquiryForm(Model model) {
        model.addAttribute("pageType","createInquiry");
        model.addAttribute("pageMode","new");

        return "layout/layout_base";
    }

    /* 문의 저장 = 제출 */
    @PostMapping("/save")
    public String saveInquiry(@ModelAttribute Inquiry inquiry, Model model) {  //todo InquiryDto로 변경

        //해당 글의 작성자 id가 현재 세션의 사용자 id인지 확인해야함

        return "redirect:/support/inquiry";
    }

    /* 문의 수정 폼 */  //todo 페이지에서 문의 상태 따라서 수정 버튼 변경해야지뭐
    @GetMapping("/edit/{inquiryId}")
    public String editInquiryForm(@PathVariable Long inquiryId, Model model) {
        //해당 글의 작성자 id가 현재 세션의 사용자 id인지 확인해야함
        //글상태가 답변완료면 수정 불가
        //InquiryDetailDto

        return "redirect:/support/inquiry";
    }

    /* 문의 수정 저장 */
    @PostMapping("/update/{inquiryId}")
    public String updateInquiry(@PathVariable Long inquiryId, @ModelAttribute Inquiry inquiry) {

        return "redirect:/support/inquiry";
    }

    /* 문의 삭제 */
    @PostMapping("/delete/{inquiryId}")
    public String deleteInquiry(@PathVariable Long inquiryId) {

        return "redirect:/support/inquiry";
    }
}
