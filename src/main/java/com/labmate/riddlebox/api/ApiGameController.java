package com.labmate.riddlebox.api;

import com.labmate.riddlebox.admindto.Question;
import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.repository.GameRepository;
import com.labmate.riddlebox.security.PrincipalDetails;
import com.labmate.riddlebox.security.SecurityUtils;
import com.labmate.riddlebox.service.GameSearchService;
import com.labmate.riddlebox.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
public class ApiGameController {

    @Autowired
    GameService gameService;

    @Autowired
    GameSearchService gameSearchService;

    @Autowired
    GameRepository gameRepository;




    // 게임 ID에 대한 질문리스트 응답
    @GetMapping("/{gameId}/getQuestions")
    @ResponseBody
    public ResponseEntity<List<Question>> getQuestions(@PathVariable("gameId") Long gameId) {
        List<Question> questions = gameService.getQuestionList(gameId);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }


    //todo: 서비스에서 오답은 따로 기록해서 오답률 높은 문제 확인하기

    // 답 단건 제출 및 채점 [3-1]
    @PostMapping("/submitAnswer")
    public ResponseEntity<?> submitAnswer(@RequestBody UserAnswerDto userAnswer) {
        Long userId = SecurityUtils.getCurrentUserId();
        if(userId==null) userId =1001L;

        //제출 답 채점
        boolean isCorrect = gameService.checkAnswer(userAnswer.getGameContentId(), userAnswer.getUserAnswer(), userId);
        //응답하기
        return ResponseEntity.ok(Collections.singletonMap("isCorrect", isCorrect));
    }



    /* [4] 게임 결과 기록 */
    @PostMapping("/{gameId}/result")
    public ResponseEntity<?> recordGameResult(@PathVariable("gameId") Long gameId,
                                              @RequestBody GameCompletionRequest gameCompletionRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        if(userId==null) userId = 1001L;

        // 게임 결과 기록
        gameService.recordGameResult(userId, gameId, gameCompletionRequest.getPlayTime(),
                                    gameCompletionRequest.getTotalQuestions(),
                                    gameCompletionRequest.getCorrectAnswersCount(),
                                    gameCompletionRequest.isFail());

        String result = gameCompletionRequest.isFail() ? "fail" : "success";

        // 결과 페이지로 리디렉션
        String redirectUrl = String.format("/game/result?totalQuestions=%d&correctAnswersCount=%d&gameResult=%s&gameId=%d",
                gameCompletionRequest.getTotalQuestions(),
                gameCompletionRequest.getCorrectAnswersCount(),
                result,
                gameId);

        return ResponseEntity.ok(Collections.singletonMap("redirectUrl", redirectUrl));
    }


    // 게임 중단 기록
    @PostMapping("/user_exit")
    public ResponseEntity<?> UserExitGate(@RequestBody GameExitRequest gameExitRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 1001L;
        if (auth != null && auth.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) auth.getPrincipal();
            userId = principalDetails.getUserPK();
        }

        //기록
        gameService.exitGameRecoding(userId, gameExitRequest.getGameId(), gameExitRequest);

        // 결과 페이지로 리디렉션
        String redirectUrl = String.format("/game/result?totalQuestions=%d&correctAnswersCount=%d&gameResult=%s&gameId=%d",
                gameExitRequest.getCorrectAnswers() + gameExitRequest.getIncorrectAnswers(),
                gameExitRequest.getCorrectAnswers(),
                "fail", gameExitRequest.getGameId());

        return ResponseEntity.ok(Collections.singletonMap("redirectUrl", redirectUrl));

        // index 페이지로 리디렉션
//        return ResponseEntity.ok(Collections.singletonMap("redirectUrl", "/game/result"));
    }


    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchGames(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<GameListDto> gamePage = gameSearchService.searchByKeyword(keyword, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("games", gamePage.getContent());
        response.put("currentPage", gamePage.getNumber());
        response.put("totalItems", gamePage.getTotalElements());
        response.put("totalPages", gamePage.getTotalPages());

        return ResponseEntity.ok(response);
    }




    // 게임 목록 상세 조회 (관리자용)
    @GetMapping
    public ResponseEntity<Page<GameListDto>> getMoreGames(@ModelAttribute GameSearchCondition condition,
                                                          @RequestParam int page,
                                                          @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GameListDto> games = gameService.searchGameSimple(condition, pageable);
        return ResponseEntity.ok(games);
    }



}