package com.ld.springsecurity.repo;

import com.ld.springsecurity.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, String> {
    List<Poll> findByRoom_Id(String roomId);
}


