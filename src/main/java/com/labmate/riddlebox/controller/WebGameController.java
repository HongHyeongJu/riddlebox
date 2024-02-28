package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.GameSearchCondition;
import com.labmate.riddlebox.dto.GameplayInfoDto;
import com.labmate.riddlebox.service.GameService;
import com.labmate.riddlebox.util.GameScoreResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;


@Controller
@RequestMapping("/game")
public class WebGameController {

    @Autowired
    GameService gameService;


    //공통 레이아웃 layout_base

    /* [1] 게임 문제 제시 페이지 */   /*TODO 지금은 스냅샷 url을 story로 하지만 이후에는 단편소설 url과 바꿔야 할듯. 현재는 문제제시의 의미로 사용*/
    @GetMapping("/{gameId}/story")
    public String getGameStory(@PathVariable("gameId") Long gameId, Model model) {
        GameplayInfoDto gameInfo = gameService.findGameInfo(gameId);
        model.addAttribute("gameInfo", gameInfo);
        model.addAttribute("pageType", "gameStory");
        model.addAttribute("title","RiddleBox ["+ gameInfo.getTitle()+" ]");
        System.out.println(" [1] 게임 스토리 페이지 ");
        System.out.println(" title "+gameInfo.getTitle());
        return "layout/layout_base";
    }


    /* [2] 게임 문제 풀이 페이지
    * 스냅샷과 단편소설이 같이 사용할 수 있음*/
    @GetMapping("/{gameId}/solve")
    public String getGameSolve(@PathVariable("gameId") Long gameId, Model model) {
        GameplayInfoDto gameInfo = gameService.findGameInfo(gameId);
        model.addAttribute("gameInfo", gameInfo);
        model.addAttribute("pageType", "gameSolve");
        model.addAttribute("title", "RiddleBox [ "+ gameInfo.getTitle()+"] 문제풀기 ");
        System.out.println("gameSolve");
        System.out.println("gameInfo.level "+gameInfo.getGameLevel());
        System.out.println("RiddleBox ["+ gameInfo.getTitle()+"] 문제풀기 ");
        return "layout/layout_base";
    }


    /* [3] 답 list 제출 및 채점 (단건은 api controller에서 개별채점) */
    @PostMapping("/submitAnswerList")
    public RedirectView showFinalResultV1(@RequestBody Map<Long, String> answers) {

        //임시 사용자 ID
        Long memberId = 0L;

        //게임결과 List 채점 + 게임 기록 저장
        GameScoreResult gameScoreResult = gameService.checkAnswers(answers, memberId);
        // todo : 나중에 이 메서드 사용할 때 isFail 여부 함께 보내줘야하는 것 잊지 말기


        //작업 완료 후 리다이렉트할 URL을 지정
        String redirectUrl = "/game/result?totalQuestions=" + gameScoreResult.getTotalQuestions() +
                             "&correctAnswersCount=" + gameScoreResult.getCorrectAnswers() +
                             "&isFail=";

        // RedirectView를 사용하여 리다이렉션을 설정
        RedirectView redirectView = new RedirectView(redirectUrl);

        return redirectView;
    }


    /* [4] 게임 결과 페이지 - 성공 or 실패 */
    @GetMapping("/{gameId}/result")
    public String gameResult(@PathVariable("gameId") Long gameId,
                             @RequestParam("totalQuestions") int totalQuestions,
                             @RequestParam("correctAnswersCount") int correctAnswersCount,
                             @RequestParam(value = "isFail", defaultValue = "false") boolean isFail,
                             Model model) {

        // 파라미터를 모델에 추가
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("correctAnswersCount", correctAnswersCount);
        model.addAttribute("gameId", gameId);
        model.addAttribute("pageType","gameResult");
        model.addAttribute("gameResult", !isFail ? "success" : "fail");

        return "layout/layout_base"; // HTML 뷰 이름
    }



    /* [5] 중도포기
    * 중도 포기 기록하고 홈페이지로 보내주기 */
    @GetMapping("/{gameId}/quit")
    public String gameQyit(@PathVariable("gameId") Long gameId) {

        //세션에서 memberId 꺼내서  GameResultType.ABANDONED
        // gameService.logGameQuit(memberId, gameId, gameResult);

        return "redirect:/"; // 홈페이지로
    }


    /* 게임 결과 페이지 => 랜덤게임 */
    @GetMapping("/random")
    public String showRandomGame() {
        Long randomGameNumber = 10L;

        return "redirect:/game/"+randomGameNumber+"/story";
    }

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