package com.ld.springsecurity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreatePostDto {
    private String content;

    private String author;

    private String roomId;

    private List<String> fileUrls;

    public CreatePostDto(String content, String author, String roomId, List<String> fileUrls) {
        this.content = content;
        this.author = author;
        this.roomId = roomId;
        this.fileUrls = fileUrls;
    }
}
