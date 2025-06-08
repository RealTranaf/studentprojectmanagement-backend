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

    List<WeeklyReportSubmission> findByReportPostIdAndIsActiveTrue(String reportPostId);

    List<WeeklyReportSubmission> findByReportPostIdInAndStudent_UsernameAndIsActiveTrue(List<String> postIds, String username);

    List<WeeklyReportSubmission> findByReportPostIdAndStudent_UsernameOrderBySubmittedAtDesc(String reportPostId, String username);
}
