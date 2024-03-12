package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.MyPageDto;
import com.labmate.riddlebox.dto.MyRecordDto;

import java.util.List;

public interface MyPageService {
    /*profile-프로필*/ MyPageDto getUserMyPageDto(long userId);

    /*profile-프로필*/ List<MyRecordDto> getUserRecordDtoList(Long userId);
}
