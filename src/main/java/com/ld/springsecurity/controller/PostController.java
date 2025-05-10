package com.ld.springsecurity.controller;

import com.ld.springsecurity.dto.CreatePostDto;
import com.ld.springsecurity.dto.EditPostDto;
import com.ld.springsecurity.model.Post;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.PostListResponse;
import com.ld.springsecurity.service.PostService;
import com.ld.springsecurity.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rooms/{roomId}/posts")
public class PostController {

    private final PostService postService;
    private final RoomService roomService;

    public PostController(PostService postService, RoomService roomService) {
        this.postService = postService;
        this.roomService = roomService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@PathVariable String roomId, @RequestBody CreatePostDto createPostDto, @AuthenticationPrincipal UserDetails userDetails){
        try{
            postService.createPost(roomId, createPostDto, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Post created successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping()
    public ResponseEntity<?> getPosts(@PathVariable String roomId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "5") int size){
        try{
            Page<Post> postList = postService.getPostsFromRoom(roomId, page, size);
            List<PostListResponse> postListResponse = postList.stream().map(PostListResponse::new).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("posts", postListResponse);
            response.put("currentPage", postList.getNumber());
            response.put("totalItems", postList.getTotalElements());
            response.put("totalPages", postList.getTotalPages());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String roomId, @PathVariable String postId, @AuthenticationPrincipal UserDetails userDetails){
        try{
            postService.deletePost(roomId, postId, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Post deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable String roomId, @PathVariable String postId,   @RequestBody EditPostDto editPostDto, @AuthenticationPrincipal UserDetails userDetails){
        try{
            postService.editPost(roomId, postId, editPostDto, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Post updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

}
