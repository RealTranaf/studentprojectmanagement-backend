package com.ld.springsecurity.controller;

import com.ld.springsecurity.dto.CreateCommentDto;
import com.ld.springsecurity.model.Comment;
import com.ld.springsecurity.model.Post;
import com.ld.springsecurity.response.CommentListResponse;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.PostListResponse;
import com.ld.springsecurity.service.CommentService;
import com.ld.springsecurity.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@PathVariable String postId, @RequestBody CreateCommentDto createCommentDto, @AuthenticationPrincipal UserDetails userDetails){
        try{
            commentService.createComment(postId, createCommentDto, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Comment created successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping()
    public ResponseEntity<?> getComments(@PathVariable String postId){
        try{
            List<Comment> commentList = commentService.getCommentsFromPost(postId);
            List<CommentListResponse> commentListResponse = commentList.stream().map(CommentListResponse::new).collect(Collectors.toList());
            return ResponseEntity.ok(commentListResponse);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

}
