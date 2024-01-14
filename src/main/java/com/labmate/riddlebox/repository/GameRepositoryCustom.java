package com.labmate.riddlebox.repository;

import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.GameSearchCondition;

import java.util.List;

public interface GameRepositoryCustom {

    List<GameListDto> search(GameSearchCondition condition);

}
