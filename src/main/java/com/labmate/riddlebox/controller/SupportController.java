package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.dto.FaqViewDto;
import com.labmate.riddlebox.dto.NoticeListDto;
import com.labmate.riddlebox.dto.NoticeViewDto;
import com.labmate.riddlebox.entity.Faq;
import com.labmate.riddlebox.service.SuppotService;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/supports")
@RequiredArgsConstructor
public class SupportController {


    private final SuppotService suppotService;

    /* 공지, 이벤트, FAQ, 1:1 모아둔 페이지 */
    @GetMapping("")
    public String supportHome(Model model) {
        List<NoticeListDto> noticeListDtoList = suppotService.getNoticeList(1, 5);
        model.addAttribute("noticeListDtoList", noticeListDtoList);
        model.addAttribute("pageType", "supportHome");
        model.addAttribute("title", "Support Home");

        return "layout/layout_base";
    }


    @GetMapping("/noticelist")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "5") int size) {
        Page<NoticeListDto> paging = suppotService.getNoticeListPaging(page, size);
        model.addAttribute("paging", paging);
        model.addAttribute("pageType", "noticeList");
        model.addAttribute("title", "Notice List");
        return "layout/layout_base";
    }


    /* 공지사항 List */
    @GetMapping("/notice")
    public String showNoticeList(@RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                 Model model) {

        if (currentPage == null) {
            currentPage = 1;
        }
        //List<CommonListDto> commonListDto =

        model.addAttribute("pageType", "noticeList");
        model.addAttribute("title", "Notice List");

        return "layout/layout_base";
    }


    /* 공지사항 상세 글 보기 */
    @GetMapping("/notice/detail/{noticeId}")
    public String showNoticeDetail(@PathVariable("noticeId") Long noticeId,
                                   Model model) {
        if (noticeId == null) {
            noticeId = 1L;
        }
        NoticeViewDto noticeViewDto = suppotService.findNoticeViewDtoById(noticeId);
        model.addAttribute("noticeViewDto", noticeViewDto);

        model.addAttribute("pageType", "noticeDetail");
        model.addAttribute("title", "Notice");
        model.addAttribute("middleContent", "board");

        return "layout/layout_base";
    }


    /* 이벤트 페이지 */
    @GetMapping("/event")
    public String showEventPage(Model model) {
        model.addAttribute("pageType", "eventPage");
        model.addAttribute("title", "EVENT");

        return "layout/layout_base";
    }


    /* FAQ List */
    @GetMapping("/faq/{faqkeyword}")
    public String showFAQList(@PathVariable("faqkeyword") String faqKeyword, Model model) {
        model.addAttribute("pageType", "faqList");
        model.addAttribute("title", "FAQ List");

        List<FaqViewDto> faqList = suppotService.getFaqList(faqKeyword);
        model.addAttribute("faqList", faqList);
        model.addAttribute("faqKeyword", faqKeyword);

        return "layout/layout_base";
    }


    /* inquiry 문의글은 따로 컨트롤러 뺌 */


}
