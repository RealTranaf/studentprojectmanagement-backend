package com.ld.springsecurity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreatePostDto {
    private String content;

    private String author;

    private String roomId;

    public CreatePostDto(String content, String author, String roomId) {
        this.content = content;
        this.author = author;
        this.roomId = roomId;
    }
}
