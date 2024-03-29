package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.MyPageDto;
import com.labmate.riddlebox.dto.MyPageProfileDto;
import com.labmate.riddlebox.dto.MyPointDto;
import com.labmate.riddlebox.dto.MyRecordDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MyPageService {
    /*profile-프로필*/ MyPageDto getUserMyPageDto(long userId);

    /*record-게임기록*/ Page<MyRecordDto> getUserRecordDtoList(Long userId, Pageable pageable) ;

    MyPageProfileDto getMyPageProfileDto(Long userId);

    /*point-포인트내역*/
    /*profile-프로필*/ List<MyPointDto> getUserPointDtoList(Long userId);
}
