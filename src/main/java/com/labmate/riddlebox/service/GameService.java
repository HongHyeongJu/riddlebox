package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.GameSearchCondition;
import com.labmate.riddlebox.dto.GameplayInfoDto;
import com.labmate.riddlebox.repository.GameRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

public interface GameService {

    public GameplayInfoDto findGameInfos(Long gameId);

    public Page<GameListDto> searchGameSimple(GameSearchCondition condition, Pageable pageable);

}
