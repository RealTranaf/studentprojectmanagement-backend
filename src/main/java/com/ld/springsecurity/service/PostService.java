package com.ld.springsecurity.service;

import com.ld.springsecurity.dto.EditPostDto;
import com.ld.springsecurity.model.Post;
import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.repo.PostRepository;
import com.ld.springsecurity.repo.RoomRepository;
import com.ld.springsecurity.repo.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final RoomRepository roomRepository;

    private final FileStorageService fileStorageService;

    public PostService(PostRepository postRepository, UserRepository userRepository, RoomRepository roomRepository, FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.fileStorageService = fileStorageService;
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

    public void createPost(String roomId, String content, List<String> fileUrls, String author) {
        Optional<User> optionalUser = userRepository.findByUsername(author);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalUser.isPresent() && optionalRoom.isPresent()){
            User user = optionalUser.get();
            Room room = optionalRoom.get();
            Post post = new Post();
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
            postRepository.delete(post);
        }
        else {
            throw new RuntimeException("Post not found");
        }
    }
    public void editPost(String roomId, String postId, String content, List<String> newFileUrls, List<String> filesToDelete , String username){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()){
            Post post = optionalPost.get();
            if (!post.getRoom().getId().equals(roomId)){
                throw new RuntimeException("Post does not belong to the specified room");
            }
            if (!post.getAuthor().getUsername().equals(username)) {
                throw new RuntimeException("You are not authorized to edit this post");
            }

            if (filesToDelete != null) {
                for (String fileUrl : filesToDelete){
                    fileStorageService.deleteFile(fileUrl);
                }
            }

            List<String> updatedFileUrls = new ArrayList<>(post.getFileUrls());
            if (newFileUrls != null) {
                updatedFileUrls.addAll(newFileUrls);
            }
            updatedFileUrls.removeAll(filesToDelete);
            post.setContent(content);
            post.setFileUrls(updatedFileUrls);
            postRepository.save(post);
        }
        else {
            throw new RuntimeException("Post not found");
        }
    }


}
