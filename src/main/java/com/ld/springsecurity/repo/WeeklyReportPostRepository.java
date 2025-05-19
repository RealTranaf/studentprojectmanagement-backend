package com.ld.springsecurity.repo;

import com.ld.springsecurity.model.WeeklyReportPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyReportPostRepository extends JpaRepository<WeeklyReportPost, String> {
    List<WeeklyReportPost> findByRoomId(String roomId);
}
