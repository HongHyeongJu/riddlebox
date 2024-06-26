package com.labmate.riddlebox.service;

import com.labmate.riddlebox.api.ApiGameController;
import com.labmate.riddlebox.dto.*;
import com.labmate.riddlebox.admindto.Question;
import com.labmate.riddlebox.util.GameScoreResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface GameService {

    public GameplayInfoDto findGameInfo(Long gameId); //단 건 게임 찾기

    void addGameViewCount(Long gameId);

    public List<Question> getQuestionList(Long gameId); //단 건 게임 질문리스트

    public Page<GameListDto> searchGameSimple(GameSearchCondition condition, Pageable pageable); //게임 검색

    public GameScoreResult checkAnswers(Map<Long, String> answers, Long memberId);  //게임 List 채점

    public boolean checkAnswer(Long gameContentId, String userAnswer, Long memberId);  //게임 단 건 채점

    public List<GameListDto> fetchRecommendedGamesForHomepage(); // 첫페이지용 게임 목록 출력


    // 게임 결과 기록 메서드
    @Transactional
    void recordGameResult( Long userId, Long gameId, int playTime, int totalQuestions, int correctAnswersCount, boolean isFail);

    @Transactional
    void exitGameRecoding(Long userId, Long gamePK, GameExitRequest gameExitRequest);

    /*게임 타입 반환하는 메서드*/
    String getGameType(Long gameId);

    GameStoryDto findGameStoryContent(Long gameId);
}
