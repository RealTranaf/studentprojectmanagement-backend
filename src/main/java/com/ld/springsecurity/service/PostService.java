package com.ld.springsecurity.service;

import com.ld.springsecurity.model.Comment;
import com.ld.springsecurity.model.Post;
import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.repo.CommentRepository;
import com.ld.springsecurity.repo.PostRepository;
import com.ld.springsecurity.repo.RoomRepository;
import com.ld.springsecurity.repo.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final RoomRepository roomRepository;

    private final CommentRepository commentRepository;

    private final FileStorageService fileStorageService;

    private final CommentService commentService;

    public PostService(PostRepository postRepository, UserRepository userRepository, RoomRepository roomRepository, CommentRepository commentRepository, FileStorageService fileStorageService, CommentService commentService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.commentRepository = commentRepository;
        this.fileStorageService = fileStorageService;
        this.commentService = commentService;
    }

    public Page<Post> getPostsFromRoom(String roomId, int page, int size){
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isPresent()){
            Room room = optionalRoom.get();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
            return postRepository.findByRoom(room, pageable);
        }
        else {
            throw new RuntimeException("Room not found");
        }
    }

    public void createPost(String roomId, String title, String content, List<String> fileUrls, String author) {
        Optional<User> optionalUser = userRepository.findByUsername(author);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalUser.isPresent() && optionalRoom.isPresent()){
            User user = optionalUser.get();
            Room room = optionalRoom.get();
            Post post = new Post();
            post.setTitle(title);
            post.setAuthor(user);
            post.setContent(content);
            post.setRoom(room);
            post.setFileUrls(fileUrls);
            postRepository.save(post);
        } else {
            throw new RuntimeException("User or room not found");
        }
    }

    public Post getPostById(String postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()){
            Post post = optionalPost.get();
            return post;
        }
        else {
            throw new RuntimeException("Post not found");
        }
    }
    public void deletePost(String roomId, String postId, String username){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()){
            Post post = optionalPost.get();
            if (!post.getRoom().getId().equals(roomId)){
                throw new RuntimeException("Post does not belong to the specified room");
            }
            if (!post.getAuthor().getUsername().equals(username)) {
                throw new RuntimeException("You are not authorized to delete this post");
            }
            if (post.getFileUrls() != null) {
                for (String fileUrl : post.getFileUrls()) {
                    fileStorageService.deleteFile(fileUrl);
                }
            }

            List<Comment> commentList = commentRepository.findByPostId(postId);
            for (Comment comment : commentList) {
                commentService.deleteComment(postId, comment.getId(), username);
            }

            postRepository.delete(post);
        }
        else {
            throw new RuntimeException("Post not found");
        }
    }
    public void editPost(String roomId, String postId, String title, String content, List<MultipartFile> files, List<String> filesToDelete , String username){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()){
            Post post = optionalPost.get();
            if (!post.getRoom().getId().equals(roomId)){
                throw new RuntimeException("Post does not belong to the specified room");
            }
            if (!post.getAuthor().getUsername().equals(username)) {
                throw new RuntimeException("You are not authorized to edit this post");
            }
            post.setTitle(title);
            post.setContent(content);
            if (filesToDelete != null) {
                for (String fileUrl : filesToDelete){
                    fileStorageService.deleteFile(fileUrl);
                    post.getFileUrls().remove(fileUrl);
                }
            }
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    post.getFileUrls().add(url);
                }
            }
            postRepository.save(post);
        }
        else {
            throw new RuntimeException("Post not found");
        }
    }


}
