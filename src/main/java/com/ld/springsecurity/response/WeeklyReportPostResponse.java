package com.ld.springsecurity.response;

import com.ld.springsecurity.model.WeeklyReportPost;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class WeeklyReportPostResponse {
    private String id;
    private String title;
    private String content;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private String author;
    private List<String> fileUrls;
    private boolean isExpired;

    public WeeklyReportPostResponse(WeeklyReportPost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.deadline = post.getDeadline();
        this.createdAt = post.getCreatedAt();
        this.author = post.getAuthor().getUsername();
        this.fileUrls = post.getFileUrls();
        this.isExpired = post.isExpired();
    }
}
