package com.ld.springsecurity.service;

import com.ld.springsecurity.dto.CreateCommentDto;
import com.ld.springsecurity.dto.CreatePostDto;
import com.ld.springsecurity.model.Comment;
import com.ld.springsecurity.model.Post;
import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.repo.CommentRepository;
import com.ld.springsecurity.repo.PostRepository;
import com.ld.springsecurity.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    public CommentService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }
    public List<Comment> getCommentsFromPost(String postId){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()){
            Post post = optionalPost.get();
            return commentRepository.findByPost(post);
        }
        else {
            throw new RuntimeException("Post not found");
        }
    }

    public void createComment(String postId, CreateCommentDto input, String author) {
        Optional<User> optionalUser = userRepository.findByUsername(author);
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            Post post = optionalPost.get();
            Comment comment = new Comment();
            comment.setContent(input.getContent());
            comment.setAuthor(user);
            comment.setPost(post);
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
}
