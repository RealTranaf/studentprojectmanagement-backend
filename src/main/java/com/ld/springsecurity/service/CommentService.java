package com.ld.springsecurity.service;

import com.ld.springsecurity.model.Comment;
import com.ld.springsecurity.model.Post;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.repo.CommentRepository;
import com.ld.springsecurity.repo.PostRepository;
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
public class CommentService {
    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final FileStorageService fileStorageService;

    public CommentService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository, FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.fileStorageService = fileStorageService;
    }
    public Page<Comment> getCommentsFromPost(String postId, int page, int size){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()){
            Post post = optionalPost.get();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
            return commentRepository.findByPost(post, pageable);
        }
        else {
            throw new RuntimeException("Post not found");
        }
    }

    public void createComment(String postId, String content, List<MultipartFile> files, String author) {
        Optional<User> optionalUser = userRepository.findByUsername(author);
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalUser.isPresent() && optionalPost.isPresent()){
            User user = optionalUser.get();
            Post post = optionalPost.get();
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setAuthor(user);
            comment.setPost(post);

            List<String> fileUrls = new ArrayList<>();
            if (files != null){
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    fileUrls.add(url);
                }
            }

            comment.setFileUrls(fileUrls);

            commentRepository.save(comment);
        } else {
            throw new RuntimeException("User or post not found");
        }
    }

    public Comment getCommentById(String commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()){
            Comment comment = optionalComment.get();
            return comment;
        }
        else {
            throw new RuntimeException("Comment not found");
        }
    }
    public void deleteComment(String postId, String commentId, String username){
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()){
            Comment comment = optionalComment.get();
            if (!comment.getPost().getId().equals(postId)){
                throw new RuntimeException("Comment does not belong to the specified post");
            }
            if (!comment.getAuthor().getUsername().equals(username)) {
                throw new RuntimeException("You are not authorized to delete this comment");
            }
            if (comment.getFileUrls() != null) {
                for (String fileUrl : comment.getFileUrls()) {
                    fileStorageService.deleteFile(fileUrl);
                }
            }
            commentRepository.delete(comment);
        }
        else {
            throw new RuntimeException("Comment not found");
        }
    }

    public void editComment(String postId, String commentId, String content, List<MultipartFile> files, List<String> filesToDelete, String username) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()){
            Comment comment = optionalComment.get();
            if (!comment.getPost().getId().equals(postId)) {
                throw new RuntimeException("Comment does not belong to the specified post");
            }

            if (!comment.getAuthor().getUsername().equals(username)) {
                throw new RuntimeException("You are not authorized to edit this comment");
            }
            comment.setContent(content);
            if (filesToDelete != null) {
                for (String fileUrl : filesToDelete) {
                    fileStorageService.deleteFile(fileUrl);
                    comment.getFileUrls().remove(fileUrl);
                }
            }
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getSize() > 100 * 1024 * 1024) {
                        throw new RuntimeException("File " + file.getOriginalFilename() + " exceeds the 100MB limit.");
                    }
                    String url = fileStorageService.storeFile(file);
                    comment.getFileUrls().add(url);
                }
            }
            commentRepository.save(comment);
        }
        else {
            throw new RuntimeException("Comment not found");
        }
    }
}
