package com.ld.springsecurity.controller;

import com.ld.springsecurity.dto.CreateCommentDto;
import com.ld.springsecurity.dto.EditCommentDto;
import com.ld.springsecurity.model.Comment;
import com.ld.springsecurity.model.Post;
import com.ld.springsecurity.response.CommentListResponse;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.PostListResponse;
import com.ld.springsecurity.service.CommentService;
import com.ld.springsecurity.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> createComment(@PathVariable String postId,
                                           @RequestParam("content") String content,
                                           @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                           @AuthenticationPrincipal UserDetails userDetails){
        try{
            commentService.createComment(postId, content, files, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Comment created successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping()
    public ResponseEntity<?> getComments(@PathVariable String postId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size){
        try{
            Page<Comment> commentList = commentService.getCommentsFromPost(postId, page, size);
            List<CommentListResponse> commentListResponse = commentList.stream().map(CommentListResponse::new).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("comments", commentListResponse);
            response.put("currentPage", commentList.getNumber());
            response.put("totalItems", commentList.getTotalElements());
            response.put("totalPages", commentList.getTotalPages());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable String postId,
                                           @PathVariable String commentId,
                                           @RequestParam("content") String content,
                                           @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                           @RequestParam(value = "filesToDelete", required = false) List<String> filesToDelete,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            commentService.editComment(postId, commentId, content, files, filesToDelete, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Comment updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String postId, @PathVariable String commentId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            commentService.deleteComment(postId, commentId, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Comment deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
