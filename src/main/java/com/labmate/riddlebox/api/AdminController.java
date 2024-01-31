package com.labmate.riddlebox.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin")
public class AdminController {

    @GetMapping("/notice/list") //공지사항 목록
    public String showNoticeList(Model model) {
        model.addAttribute("pageType", "noticeList");
        model.addAttribute("title", "RiddleBox Notice");

        return "layout/admin/admin_layout_base";
    }


    @GetMapping("/notice/new") //공지사항 작성
    public String showNoticeDetail(Model model) {
        model.addAttribute("pageType", "noticeDetail");
        model.addAttribute("pageMode", "new");
        model.addAttribute("title", "RiddleBox Notice");

        return "layout/admin/admin_layout_base";
    }


}
