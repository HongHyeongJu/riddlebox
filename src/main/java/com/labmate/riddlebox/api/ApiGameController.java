package com.labmate.riddlebox.api;

import com.labmate.riddlebox.admindto.Question;
import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.security.PrincipalDetails;
import com.labmate.riddlebox.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class ApiGameController {

    @Autowired
    GameService gameService;

    // 게임 목록 조회
    @GetMapping
    public ResponseEntity<Page<GameListDto>> getMoreGames(@ModelAttribute GameSearchCondition condition,
                                                          @RequestParam int page,
                                                          @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GameListDto> games = gameService.searchGameSimple(condition, pageable);
        return ResponseEntity.ok(games);
    }


    @GetMapping("/{gameId}/getQuestions")
    @ResponseBody
    public ResponseEntity<List<Question>> getQuestions(@PathVariable("gameId") Long gameId) {
        List<Question> questions = gameService.getQuestionList(gameId);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }


    // 답 단건 제출 및 채점 [3-1]    //스프링시큐리티는 일단 주석처리
    @PostMapping("/submitAnswer")
    public ResponseEntity<AnswerResponse> submitAnswer(@RequestBody UserAnswerDto userAnswer) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 1001L;
        if (auth != null && auth.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) auth.getPrincipal();
            userId = principalDetails.getUserPK();
        }


        //제출 답 채점(서비스에서 오답은 따로 기록해두기)
        boolean isCorrect = gameService.checkAnswer(userAnswer.getGameContentId(), userAnswer.getUserAnswer(), userId);
        System.out.println("컨트롤러 submitAnswer isCorrect " + isCorrect);
        //응답하기
        return ResponseEntity.ok().body(new AnswerResponse(isCorrect));
    }


}