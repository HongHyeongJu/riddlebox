package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.GameSearchCondition;
import com.labmate.riddlebox.dto.GameplayInfoDto;
import com.labmate.riddlebox.dto.UserAnswerDto;
import com.labmate.riddlebox.repository.GameRepository;
import com.labmate.riddlebox.util.GameScoreResult;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface GameService {

    public GameplayInfoDto findGameInfos(Long gameId); //단 건 게임 찾기

    public Page<GameListDto> searchGameSimple(GameSearchCondition condition, Pageable pageable); //게임 검색

    public GameScoreResult checkAnswers(List<UserAnswerDto> answers);  //게임 List 채점

    public boolean checkAnswer(UserAnswerDto answer);  //게임 단 건 채점

    public List<GameListDto> fetchRecommendedGamesForHomepage(); // 첫페이지용 게임 목록 출력



}
