package com.ld.springsecurity.response;

import com.ld.springsecurity.model.Poll;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class PollResponse {
    private String id;
    private String title;
    private String description;
    private List<String> fileUrls;
    private List<String> options;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;

    public PollResponse(Poll poll) {
        this.id = poll.getId();
        this.title = poll.getTitle();
        this.description = poll.getDescription();
        this.fileUrls = poll.getFileUrls();
        this.options = poll.getOptions();
        this.createdBy = poll.getCreatedBy().getUsername();
        this.createdAt = poll.getCreatedAt();
        this.deadline = poll.getDeadline();
    }
}
