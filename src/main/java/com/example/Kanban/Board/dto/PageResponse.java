package com.example.Kanban.Board.dto;

import java.util.List;

public class PageResponse<T> {
    
    private List<T> content;
    private Long totalCount;
            
    public PageResponse() {
    }
    
    public PageResponse(List<T> content, Long totalCount) {
        this.content = content;
        this.totalCount = totalCount;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}