package com.ld.springsecurity.response;

import com.ld.springsecurity.model.Topic;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TopicResponse {
    private String id;
    private String title;
    private String description;
    private List<String> fileUrls;
    private String proposedBy;

    public TopicResponse(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.description = topic.getDescription();
        this.fileUrls = topic.getFileUrls();
        this.proposedBy = topic.getProposedBy().getUsername();
    }
}
