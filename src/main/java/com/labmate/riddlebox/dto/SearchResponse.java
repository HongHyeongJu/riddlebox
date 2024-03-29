package com.labmate.riddlebox.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {
    private List<GameListDto> results;
    private int currentPage;
    private int totalPages;

    public SearchResponse(List<GameListDto> content, int number, int totalPages) {
    }

    // 생성자, Getter와 Setter 생략
}
