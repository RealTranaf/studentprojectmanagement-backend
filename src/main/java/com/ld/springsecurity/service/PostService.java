package com.ld.springsecurity.service;

import com.ld.springsecurity.dto.CreatePostDto;
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

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final RoomRepository roomRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, RoomRepository roomRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
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

    public void createPost(String roomId, CreatePostDto input, String author) {
        Optional<User> optionalUser = userRepository.findByUsername(author);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalUser.isPresent() && optionalRoom.isPresent()){
            User user = optionalUser.get();
            Room room = optionalRoom.get();
            Post post = new Post();
            post.setAuthor(user);
            post.setContent(input.getContent());
            post.setRoom(room);
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
            postRepository.delete(post);
        }
        else {
            throw new RuntimeException("Post not found");
        }
    }
    public void editPost(String roomId, String postId, EditPostDto editPostDto, String username){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()){
            Post post = optionalPost.get();
            if (!post.getRoom().getId().equals(roomId)){
                throw new RuntimeException("Post does not belong to the specified room");
            }
            if (!post.getAuthor().getUsername().equals(username)) {
                throw new RuntimeException("You are not authorized to edit this post");
            }
//            System.out.println("Username: " + username);
//            System.out.println("Post Author: " + post.getAuthor());
            post.setContent(editPostDto.getContent());
            postRepository.save(post);
        }
        else {
            throw new RuntimeException("Post not found");
        }
    }
}
