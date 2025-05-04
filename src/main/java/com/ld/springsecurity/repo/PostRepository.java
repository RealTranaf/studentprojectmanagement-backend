package com.ld.springsecurity.repo;

import com.ld.springsecurity.model.Post;
import com.ld.springsecurity.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {
    List<Post> findByRoom(Room room);
}
