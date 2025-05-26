package com.ld.springsecurity.response;

import com.ld.springsecurity.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostResponse {
    private String id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdTime;
    private List<String> fileUrls;

    public PostResponse(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor().getUsername();
        this.createdTime = post.getCreatedTime();
        this.fileUrls = post.getFileUrls();
    }
}
