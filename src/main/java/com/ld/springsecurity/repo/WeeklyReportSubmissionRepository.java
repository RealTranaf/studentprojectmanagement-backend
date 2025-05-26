package com.ld.springsecurity.repo;

import com.ld.springsecurity.model.WeeklyReportPost;
import com.ld.springsecurity.model.WeeklyReportSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyReportSubmissionRepository extends JpaRepository<WeeklyReportSubmission, String> {
    List<WeeklyReportSubmission> findByReportPostId(String reportPostId);

    List<WeeklyReportSubmission> findByReportPostIdOrderBySubmittedAtAsc(String reportPostId);

    Optional<WeeklyReportSubmission> findByReportPost_IdAndStudent_Id(String reportPostId, String studentId);

    List<WeeklyReportSubmission> findByReportPostIdInAndStudent_Username(List<String> postIds, String username);
}
