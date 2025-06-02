package com.ld.springsecurity.repo;

import com.ld.springsecurity.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, String> {
    List<Topic> findByIsCustomFalse();
}
