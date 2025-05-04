package com.ld.springsecurity.response;

import com.ld.springsecurity.model.Comment;
import com.ld.springsecurity.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentListResponse {
    private String id;
    private String content;
    private String author;
    private LocalDateTime createdTime;

    public CommentListResponse(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = comment.getAuthor().getUsername();
        this.createdTime = comment.getCreatedTime();
    }
}
