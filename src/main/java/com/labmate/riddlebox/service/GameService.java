package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.GameSearchCondition;
import com.labmate.riddlebox.dto.GameplayInfoDto;
import com.labmate.riddlebox.admindto.Question;
import com.labmate.riddlebox.util.GameScoreResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface GameService {

    public GameplayInfoDto findGameInfo(Long gameId); //단 건 게임 찾기
    public List<Question> getQuestionList(Long gameId); //단 건 게임 질문리스트

    public Page<GameListDto> searchGameSimple(GameSearchCondition condition, Pageable pageable); //게임 검색

    public GameScoreResult checkAnswers(Map<Long, String> answers, Long memberId);  //게임 List 채점

    public boolean checkAnswer(Long gameContentId, String userAnswer, Long memberId);  //게임 단 건 채점

    public List<GameListDto> fetchRecommendedGamesForHomepage(); // 첫페이지용 게임 목록 출력


    // 게임 결과 기록 메서드
    @Transactional
    void recordGameResult(Long gameId, Long userId, int totalQuestions, int correctAnswersCount, boolean isFail);
}
