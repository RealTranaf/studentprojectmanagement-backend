package com.ld.springsecurity.response;

import com.ld.springsecurity.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostListResponse {
    private String id;
    private String content;
    private String author;
    private LocalDateTime createdTime;

    public PostListResponse(Post post){
        this.id = post.getId();
        this.content = post.getContent();
        this.author = post.getAuthor().getUsername();
        this.createdTime = post.getCreatedTime();
    }
}
