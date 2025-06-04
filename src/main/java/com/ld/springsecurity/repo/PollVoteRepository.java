package com.ld.springsecurity.repo;

import com.ld.springsecurity.model.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVote, String> {
    List<PollVote> findByPoll_Id(String pollId);
    Optional<PollVote> findByPoll_IdAndUser_Id(String pollId, String userId);
}
