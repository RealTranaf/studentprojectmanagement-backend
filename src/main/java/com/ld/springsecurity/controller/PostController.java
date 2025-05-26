package com.ld.springsecurity.controller;

import com.ld.springsecurity.model.Post;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.PostResponse;
import com.ld.springsecurity.service.FileStorageService;
import com.ld.springsecurity.service.PostService;
import com.ld.springsecurity.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rooms/{roomId}/posts")
public class PostController {

    private final PostService postService;
    private final RoomService roomService;
    private final FileStorageService fileStorageService;

    public PostController(PostService postService, RoomService roomService, FileStorageService fileStorageService) {
        this.postService = postService;
        this.roomService = roomService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@PathVariable String roomId,
                                        @RequestParam("title") String title,
                                        @RequestParam("content") String content,
                                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                        @AuthenticationPrincipal UserDetails userDetails){
        try{
            List<String> fileUrls = new ArrayList<>();
            if (files != null && !files.isEmpty()){
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024){
                        throw new RuntimeException("File size exceeds the 100MB limit.");
                    }
                    String fileUrl = fileStorageService.storeFile(file);
                    fileUrls.add(fileUrl);
                }
            }
            postService.createPost(roomId, title, content, fileUrls, userDetails.getUsername());
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
            List<PostResponse> postListResponse = postList.stream().map(PostResponse::new).collect(Collectors.toList());

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
    public ResponseEntity<?> updatePost(@PathVariable String roomId,
                                        @PathVariable String postId,
                                        @RequestParam("title") String title,
                                        @RequestParam("content") String content,
                                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                        @RequestParam(value = "filesToDelete", required = false) List<String> filesToDelete,
                                        @AuthenticationPrincipal UserDetails userDetails){
        try{
            postService.editPost(roomId, postId, title, content, files, filesToDelete, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Post updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

}
