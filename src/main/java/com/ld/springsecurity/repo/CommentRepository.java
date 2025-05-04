package com.ld.springsecurity.repo;

import com.ld.springsecurity.model.Comment;
import com.ld.springsecurity.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByPost(Post post);
}
