package com.labmate.riddlebox.api;

import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.entity.GameContent;
import com.labmate.riddlebox.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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


    @GetMapping("/{gameId}/solve/contents")
    @ResponseBody
    public List<String> getGameContentList(@PathVariable("gameId") Long gameId) {
        List<String> questionList = gameService.getQuestionList(gameId);
        return questionList;
    }


    // 답 단건 제출 및 채점 [3-1]    //스프링시큐리티는 일단 주석처리
    @PostMapping("/submitAnswer")
    public ResponseEntity<AnswerResponse> submitAnswer(@PathVariable Long gameId,
                                                       @RequestBody UserAnswerDto answer,
//                                                       Authentication authentication,
                                                       HttpServletRequest request) {
//        // 인증 확인
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        // 사용자 식별자
//        String memberId = authentication.getMemberId();


        //제출 답 채점(서비스에서 오답은 따로 기록해두기)
//        boolean isCorrect = gameService.checkAnswer(gameId, answer, memberId);
        boolean isCorrect = gameService.checkAnswer(answer);

        //헤더에서 진행상황 뽑고 누적하기
        int totalQuestions = Integer.parseInt(request.getHeader("Total-Questions"));
        int correctAnswersCount = Integer.parseInt(request.getHeader("Correct-Answers-Count"));

        //업데이트
        totalQuestions++;
        if (isCorrect) {
            correctAnswersCount++;
        }

        // 업데이트된 정보를 응답 헤더에 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Total-Questions", String.valueOf(totalQuestions));
        headers.add("Correct-Answers-Count", String.valueOf(correctAnswersCount));


        //응답하기
        return ResponseEntity.ok().headers(headers).body(new AnswerResponse(isCorrect));
    }


}