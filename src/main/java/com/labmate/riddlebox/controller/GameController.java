package com.labmate.riddlebox.controller;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.GameSearchCondition;
import com.labmate.riddlebox.dto.GameplayInfoDto;
import com.labmate.riddlebox.dto.UserAnswerDto;
import com.labmate.riddlebox.entity.Game;
import com.labmate.riddlebox.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/games")
public class GameController {

    @Autowired
    GameService gameService;

    // 게임 목록 조회
    @GetMapping
    public String showGames(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GameListDto> games = gameService.searchGameSimple(new GameSearchCondition(), pageable);
        model.addAttribute("games", games);
        return "games";
    }


    // 게임 문제 제시 페이지 [1]
    @GetMapping("/{gameId}/story")
    public String getGameStory(@PathVariable Long gameId, Model model) {
        GameplayInfoDto gameplayInfoDto = gameService.findGameInfos(gameId);
        model.addAttribute("gameplayInfoDto", gameplayInfoDto);
        return "gamePlayStory";
    }

    // 게임 문제 풀이 페이지 [2]
    @GetMapping("/{gameId}/solve")
    public String getGameSolve(@PathVariable Long gameId, Model model) {
        GameplayInfoDto gameplayInfoDto = gameService.findGameInfos(gameId);
        model.addAttribute("gameplayInfoDto", gameplayInfoDto);
        return "gamePlaySolve";
    }

    // 게임 결과 페이지 [3] 답 list 제출 (단건은 api controller에)
    @PostMapping("/{gameId}/submitList/result")
    public String  showFinalResult(@PathVariable Long gameId,
                                   @RequestBody List<UserAnswerDto> answers,
                                   Model model) {

        //게임결과 채점

        //게임 기록 저장

        GameResult gameResult = getSessionGameResult(session, gameId); // 세션에서 최종 결과 가져오기
        model.addAttribute("gameResult", gameResult);
        return "gamePlayResult";
    }


    // 게임 결과 페이지 [3] 단건 채점 완료 후 결과 페이지로
    @GetMapping("/{gameId}/submitSingle/result")
    public String getGameResult(@PathVariable Long gameId,
                                @RequestParam int correctScore,
                                @RequestParam int question, Model model) {
        model.addAttribute("correctScore", correctScore);
        model.addAttribute("question", question);
        return "gamePlayResult";
    }



    private void updateSessionWithAnswer(HttpSession session, Long gameId, boolean isCorrect) {
    // 세션에 사용자의 현재 게임 상태 업데이트 (예: 정답 수, 시도 횟수 등)
    // 게임 상태를 세션에서 관리하는 로직 구현
    }

    private GameResult getSessionGameResult(HttpSession session, Long gameId) {
    // 세션에서 사용자의 게임 결과를 가져오는 로직 구현
    // 여기서는 예시로 GameResult 객체를 반환한다고 가정
    return (GameResult) session.getAttribute("gameResult_" + gameId);
    }

}
