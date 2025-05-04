package com.ld.springsecurity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCommentDto {
    private String content;

    private String author;

    private String postId;

    public CreateCommentDto(String content, String author, String postId) {
        this.content = content;
        this.author = author;
        this.postId = postId;
    }

}
