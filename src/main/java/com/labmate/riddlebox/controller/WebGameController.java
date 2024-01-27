package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.GameSearchCondition;
import com.labmate.riddlebox.dto.GameplayInfoDto;
import com.labmate.riddlebox.service.GameService;
import com.labmate.riddlebox.util.GameScoreResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/game")
public class WebGameController {

    @Autowired
    GameService gameService;

    // 게임 목록 조회
    @GetMapping("/games")
    public String showGames(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GameListDto> games = gameService.searchGameSimple(new GameSearchCondition(), pageable);
        model.addAttribute("games", games);
        return "games";
    }


    //공통 레이아웃 layout_base

    // 게임 문제 제시 페이지 [1]
    @GetMapping("/{gameId}/story")
    public String getGameStory(@PathVariable("gameId") Long gameId, Model model) {
        GameplayInfoDto gameInfo = gameService.findGameInfo(gameId);
        model.addAttribute("gameInfo", gameInfo);
        model.addAttribute("pageType", "gameStory");
        model.addAttribute("title","RiddleBox ["+ gameInfo.getTitle()+" ]");
        System.out.println(" [1] 게임 스토리 페이지 ");
        System.out.println(" title "+gameInfo.getTitle());
        return "layout_base";
    }

    // 게임 문제 풀이 페이지 [2]
    @GetMapping("/{gameId}/solve")
    public String getGameSolve(@PathVariable("gameId") Long gameId, Model model) {
        GameplayInfoDto gameInfo = gameService.findGameInfo(gameId);
        model.addAttribute("gameInfo", gameInfo);
        model.addAttribute("pageType", "gameSolve");
        model.addAttribute("title", "RiddleBox [ "+ gameInfo.getTitle()+"] 문제풀기 ");
        System.out.println("gameSolve");
        System.out.println("gameInfo.level "+gameInfo.getGameLevel());
        System.out.println("RiddleBox ["+ gameInfo.getTitle()+"] 문제풀기 ");
        return "layout_base";
    }



    // 게임 결과 페이지 [4]
    @GetMapping("/result")
    public String gameResult(@RequestParam("totalQuestions") int totalQuestions,
                             @RequestParam("correctAnswersCount") int correctAnswersCount,
                             Model model) {

        // 파라미터를 모델에 추가
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("correctAnswersCount", correctAnswersCount);

        return "gamePlayResult"; // HTML 뷰 이름
    }



    // 답 list 제출 및 채점 [3] (단건은 api controller에서 개별채점)
    @PostMapping("/submitAnswerList")
    public RedirectView showFinalResultV1(@RequestBody Map<Long, String> answers,
                                          RedirectAttributes redirectAttributes) {

        //임시 사용자 ID
        Long memberId = 0L;

        //게임결과 List 채점 + 게임 기록 저장
        GameScoreResult gameScoreResult = gameService.checkAnswers(answers, memberId);

        // 정보를 리다이렉트 어트리뷰트에 추가합니다.
        redirectAttributes.addFlashAttribute("totalQuestions", gameScoreResult.getTotalQuestions());
        redirectAttributes.addFlashAttribute("correctAnswers", gameScoreResult.getCorrectAnswers());


        //작업 완료 후 리다이렉트할 URL을 지정
        String redirectUrl = "/game/result";

        // RedirectView를 사용하여 리다이렉션을 설정
        RedirectView redirectView = new RedirectView(redirectUrl);

        return redirectView;
    }



    // 단건 채점 마지막 [3-2]
/*    @PostMapping("/submitAnswerFinal")
    public String showFinalResultV2(@RequestBody UserAnswerDto answer,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes){

        //게임결과 단 건 채점
        boolean isCorrect = gameService.checkAnswer(answer);

        //헤더에서 진행상황 뽑고 누적하기
        int totalQuestions = Integer.parseInt(request.getHeader("Total-Questions"));
        int correctAnswersCount = Integer.parseInt(request.getHeader("Correct-Answers-Count"));

        //업데이트
        totalQuestions++;
        if (isCorrect) {
            correctAnswersCount++;
        }

        // 업데이트된 최종정보를 뷰로 전달
        redirectAttributes.addAttribute("totalQuestions", totalQuestions);
        redirectAttributes.addAttribute("correctAnswersCount", correctAnswersCount);
        return "redirect:/game/result";
    }*/





}
