package com.labmate.riddlebox.api;

import com.labmate.riddlebox.admindto.Question;
import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.enumpackage.GameResultType;
import com.labmate.riddlebox.security.PrincipalDetails;
import com.labmate.riddlebox.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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


    // 답 단건 제출 및 채점 [3-1]
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
        //응답하기
        return ResponseEntity.ok().body(new AnswerResponse(isCorrect));
    }


    /* [4] 게임 결과 기록 */
    @PostMapping("/{gameId}/result")
    public ResponseEntity<?> recordGameResult(@PathVariable("gameId") Long gameId,
                                              @RequestBody GameCompletionRequest gameCompletionRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 1001L;
        if (auth != null && auth.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) auth.getPrincipal();
            userId = principalDetails.getUserPK();
        }
        System.out.println("userId " + userId);

        // 게임 결과 기록
        gameService.recordGameResult(userId, gameId, gameCompletionRequest.getPlayTime(),
                gameCompletionRequest.getTotalQuestions(),
                gameCompletionRequest.getCorrectAnswersCount(),
                gameCompletionRequest.isFail());

        String result = gameCompletionRequest.isFail() ? "fail" : "success";
        String gameType = gameService.getGameType(gameId);
        // 결과 페이지로 리디렉션
        String redirectUrl = String.format("/game/result?totalQuestions=%d&correctAnswersCount=%d&gameResult=%s&gameId=%d",
                gameCompletionRequest.getTotalQuestions(),
                gameCompletionRequest.getCorrectAnswersCount(),
                result, gameId);

        return ResponseEntity.ok(Collections.singletonMap("redirectUrl", redirectUrl));


    }


    // 게임 중단 기록
    @PostMapping("/user_exit")
    public ResponseEntity<?> UserExitGate(@RequestBody GameExitRequest gameExitRequest) {
        System.out.println(gameExitRequest.toString());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 1001L;
        if (auth != null && auth.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) auth.getPrincipal();
            userId = principalDetails.getUserPK();
        }

        //기록
        gameService.exitGameRecoding(userId, gameExitRequest.getGamePK(), gameExitRequest);


        // 결과 페이지로 리디렉션
        String redirectUrl = String.format("/game/result?totalQuestions=%d&correctAnswersCount=%d&gameResult=%s&gameId=%d",
                gameExitRequest.getCorrectAnswers()+gameExitRequest.getIncorrectAnswers(),
                gameExitRequest.getCorrectAnswers(),
                "fail", gameExitRequest.getGamePK());

        return ResponseEntity.ok(Collections.singletonMap("redirectUrl", redirectUrl));

        // index 페이지로 리디렉션
//        return ResponseEntity.ok(Collections.singletonMap("redirectUrl", "/game/result"));
    }


}