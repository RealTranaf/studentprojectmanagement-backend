package com.ld.springsecurity.controller;

import com.ld.springsecurity.dto.CreatePostDto;
import com.ld.springsecurity.model.Post;
import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.response.CreateRoomResponse;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.PostListResponse;
import com.ld.springsecurity.response.RoomListResponse;
import com.ld.springsecurity.service.PostService;
import com.ld.springsecurity.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<?> getPosts(@PathVariable String roomId){
        try{
            List<Post> postList = postService.getPostsFromRoom(roomId);
            List<PostListResponse> postListResponse = postList.stream().map(PostListResponse::new).collect(Collectors.toList());
            return ResponseEntity.ok(postListResponse);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

}
