package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.GameSearchCondition;
import com.labmate.riddlebox.dto.GameplayInfoDto;
import com.labmate.riddlebox.dto.UserAnswerDto;
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

import java.util.List;


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


    // 게임 문제 제시 페이지 [1]
    @GetMapping("/{gameId}/story")
    public String getGameStory(@PathVariable("gameId") Long gameId, Model model) {
        GameplayInfoDto gameplayInfoDto = gameService.findGameInfos(gameId);
        model.addAttribute("gameplayInfoDto", gameplayInfoDto);
//        return "gamePlayStory";
        return "test";
    }

    // 게임 문제 풀이 페이지 [2]
    @GetMapping("/{gameId}/solve")
    public String getGameSolve(@PathVariable Long gameId, Model model) {
        GameplayInfoDto gameplayInfoDto = gameService.findGameInfos(gameId);
        model.addAttribute("gameplayInfoDto", gameplayInfoDto);
        return "gamePlaySolve";
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
    public String showFinalResultV1(@RequestBody List<UserAnswerDto> answers,
                                    RedirectAttributes redirectAttributes) {

        //게임결과 List 채점 + 게임 기록 저장
        GameScoreResult gameScoreResult = gameService.checkAnswers(answers);

        //게임 기록 저장
        int totalQuestions = gameScoreResult.getTotalQuestions() ;
        int correctAnswersCount = gameScoreResult.getTotalQuestions() ;

        redirectAttributes.addAttribute("totalQuestions", totalQuestions);
        redirectAttributes.addAttribute("correctAnswersCount", correctAnswersCount);
        return "redirect:/game/result";
    }


    // 단건 채점 마지막 [3-2]
    @PostMapping("/submitAnswerFinal")
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
    }





}
