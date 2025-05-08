package com.ld.springsecurity.repo;

import com.ld.springsecurity.model.Post;
import com.ld.springsecurity.model.Room;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {
    Page<Post> findByRoom(Room room, Pageable pageable);
}
